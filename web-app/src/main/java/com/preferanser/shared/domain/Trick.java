package com.preferanser.shared.domain;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Lists.newLinkedList;
import static com.google.common.collect.Maps.newLinkedHashMap;

public class Trick implements Iterable<Turn> {

    private final TurnRotator turnRotator;
    private final LinkedList<Turn> turns = newLinkedList();
    private final Players players;
    private int currentTurnIndex;

    public Trick(Players players, TurnRotator turnRotator, Map<Card, Hand> centerCardHandMap) {
        this.currentTurnIndex = -1;
        this.players = players;
        this.turnRotator = turnRotator;
        for (Map.Entry<Card, Hand> cardHandEntry : centerCardHandMap.entrySet()) {
            turns.add(new Turn(cardHandEntry.getKey(), cardHandEntry.getValue()));
            currentTurnIndex++;
        }
    }

    public Trick(Players players, TurnRotator handEnumRotator) {
        this(players, handEnumRotator, ImmutableMap.<Card, Hand>of());
    }

    public Card getHandCard(Hand hand) {
        for (Turn turn : turns())
            if (turn.getHand() == hand)
                return turn.getCard();
        return null;
    }

    public boolean hasCardFrom(Hand hand) {
        return getHandCard(hand) != null;
    }

    public void applyTurn(Hand fromHand, Card card) {
        checkState(turnRotator.current() == fromHand, "Can't turn from " + fromHand + " as current turn makes " + turnRotator.current());
        checkArgument(!hasCardFrom(fromHand), fromHand + " can't make a turn (" + card + ") - already made its turn (" + getHandCard(fromHand) + ")");
        if (hasRedoTurns())
            turns.subList(currentTurnIndex + 1, turns.size()).clear();
        turns.addLast(new Turn(fromHand, card));
        currentTurnIndex++;
        turnRotator.next();
    }

    public boolean hasUndoTurns() {
        return !turns().isEmpty();
    }

    public boolean hasRedoTurns() {
        return turns.size() - 1 > currentTurnIndex;
    }

    public Turn undoTurn() {
        Preconditions.checkState(!turns.isEmpty(), "Can't revert last turn in the trick because trick is empty");
        turnRotator.prev();
        return turns.get(currentTurnIndex--);
    }

    public Turn redoTurn() {
        Preconditions.checkState(hasRedoTurns(), "There is no turns in the trick to redo");
        turnRotator.next();
        return turns.get(++currentTurnIndex);
    }

    public Optional<Hand> determineTrickWinner(Optional<Suit> optionalTrump) {
        Optional<Turn> winningTurn = Optional.absent();
        for (Turn turn : turns()) {
            if (!winningTurn.isPresent()) {
                winningTurn = Optional.of(turn);
            } else {
                if (optionalTrump.isPresent() && winningTurn.get().getSuit() != optionalTrump.get() && turn.getSuit() == optionalTrump.get()) {
                    winningTurn = Optional.of(turn);
                } else if (winningTurn.get().getSuit() == turn.getSuit()) {
                    if (Rank.comparator().compare(winningTurn.get().getRank(), turn.getRank()) < 0) {
                        winningTurn = Optional.of(turn);
                    }
                }
            }
        }

        if (!winningTurn.isPresent()) {
            return Optional.absent();
        } else {
            return Optional.of(winningTurn.get().getHand());
        }
    }

    public Hand getTurn() {
        return turnRotator.current();
    }

    public Optional<Suit> getSuit() {
        Iterator<Turn> turnIterator = turns().iterator();
        if (!turnIterator.hasNext())
            return Optional.absent();
        else
            return Optional.of(turnIterator.next().getCard().getSuit());
    }

    public Map<Card, Hand> asMap() {
        Map<Card, Hand> map = newLinkedHashMap();
        for (Turn turn : turns())
            map.put(turn.getCard(), turn.getHand());
        return ImmutableMap.copyOf(map);
    }

    public boolean isEmpty() {
        return turns().isEmpty();
    }

    private List<Turn> turns() {
        if (turns.isEmpty())
            return turns;
        return turns.subList(0, currentTurnIndex + 1);
    }

    public void clearTurnLog() {
        turns.clear();
    }

    public int getTurnCount() {
        return turns().size();
    }

    public TurnRotator getTurnRotator() {
        return turnRotator;
    }

    @Override public Iterator<Turn> iterator() {
        return turns.iterator();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Trick that = (Trick) o;

        return Objects.equal(this.turnRotator, that.turnRotator) &&
            Objects.equal(this.turns, that.turns) &&
            Objects.equal(this.players, that.players);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(turnRotator, turns, players);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
            .addValue(turnRotator)
            .addValue(turns)
            .addValue(players)
            .addValue(currentTurnIndex)
            .toString();
    }
}
