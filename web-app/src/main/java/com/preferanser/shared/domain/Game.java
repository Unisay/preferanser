/*
 * Preferanser is a program to simulate and calculate Russian Preferans Card game deals.
 *
 *     Copyright (C) 2013  Yuriy Lazarev <Yuriy.Lazarev@gmail.com>
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see [http://www.gnu.org/licenses/].
 */

package com.preferanser.shared.domain;

import com.google.common.base.Optional;
import com.google.common.collect.*;
import com.preferanser.shared.domain.exception.*;
import com.preferanser.shared.util.EnumRotator;
import com.preferanser.shared.util.GameUtils;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;

/**
 * Represents game state
 */
public class Game {

    private final Players players;
    private final Widow widow;
    private final Map<Hand, Contract> handContracts;
    private final Multimap<Hand, Card> handCardMultimap;
    private final LinkedList<Trick> trickLog = Lists.newLinkedList();
    private int currentTrickIndex;

    Game(Players players,
         Widow widow,
         Map<Hand, Contract> handContracts,
         EnumRotator<Hand> turnRotator,
         Multimap<Hand, Card> handCardMultimap,
         Map<Card, Hand> centerCardHandMap
    ) {
        this.players = players;
        this.widow = new Widow(widow);
        this.handContracts = newHashMap(handContracts);
        this.handCardMultimap = LinkedHashMultimap.create(handCardMultimap);
        trickLog.add(new Trick(players, turnRotator, centerCardHandMap));
        currentTrickIndex = 0;
    }

    public Map<Hand, Set<Card>> getHandCards() {
        return GameUtils.copyDefensive(handCardMultimap);
    }

    public Collection<Card> getCardsByHand(Hand hand) {
        return ImmutableList.copyOf(handCardMultimap.get(hand));
    }

    public Map<Card, Hand> getCenterCards() {
        return currentTrick().asMap();
    }

    public Map<Hand, Integer> getHandTrickCounts() {
        Map<Hand, Integer> counts = Maps.newHashMap();
        for (Hand hand : Hand.values())
            counts.put(hand, getHandTricksCount(hand));
        return counts;
    }

    public int getHandTricksCount(Hand hand) {
        int count = 0;
        Optional<Suit> trump = getTrump();
        for (Trick nextTrick : trickLog.subList(0, currentTrickIndex + 1)) {
            Optional<Hand> maybeWinner = nextTrick.determineTrickWinner(trump);
            if (maybeWinner.isPresent() && maybeWinner.get() == hand)
                count++;
        }
        return count;
    }

    public Map<Hand, Contract> getHandContracts() {
        return ImmutableMap.copyOf(handContracts);
    }

    private Optional<Contract> getPlayingContract() {
        for (Contract contract : handContracts.values()) {
            if (contract.isPlaying()) {
                return Optional.of(contract);
            }
        }
        return Optional.absent();
    }

    public Optional<Suit> getTrump() {
        if (getPlayingContract().isPresent()) {
            Contract contract = getPlayingContract().get();
            assert contract.isPlaying();
            return contract.getTrump();
        }
        return Optional.absent();
    }

    public void makeTurn(Hand fromHand, Card card) throws GameException {
        if (fromHand != currentTrick().getTurn())
            throw new NotInTurnException(currentTrick().getTurn(), fromHand);

        if (!handCardMultimap.containsEntry(fromHand, card))
            throw new NoSuchHandCardException(fromHand, card);

        if (currentTrick().hasCardFrom(fromHand))
            throw new DuplicateGameTurnException(currentTrick().asMap(), fromHand);

        if (currentTrick().asMap().size() == players.getNumPlayers())
            throw new NoTurnsAllowedException(currentTrick().asMap());

        validateHandTurn(fromHand, card);

        boolean removed = handCardMultimap.get(fromHand).remove(card);
        assert removed : "Failed to remove " + card + " from " + fromHand;
        currentTrick().applyTurn(fromHand, card);
    }

    // TODO instead of throwing exceptions - return validation errors
    private void validateHandTurn(Hand hand, Card card) throws IllegalSuitException {
        Optional<Suit> maybeTrickSuit = currentTrick().getSuit();
        if (!maybeTrickSuit.isPresent())
            return;

        Suit trickSuit = maybeTrickSuit.get();
        if (trickSuit == card.getSuit())
            return;

        if (handHasSuit(hand, trickSuit))
            throw new IllegalSuitException(trickSuit, card.getSuit());

        Optional<Suit> maybeTrump = getTrump();
        if (!maybeTrump.isPresent())
            return;

        Suit trump = maybeTrump.get();
        if (card.getSuit() != trump && handHasSuit(hand, trump))
            throw new IllegalSuitException(trump, card.getSuit());
    }

    private boolean handHasSuit(Hand hand, Suit suit) {
        for (Card card : handCardMultimap.get(hand))
            if (card.getSuit() == suit)
                return true;
        return false;
    }

    public boolean sluffTrick() {
        if (currentTrick().isOpen())
            return false;

        Optional<Hand> trickWinner = currentTrick().determineTrickWinner(getTrump());
        assert trickWinner.isPresent();
        Trick trick = new Trick(players, new EnumRotator<Hand>(Hand.values(), trickWinner.get()));
        trickLog.add(trick);
        currentTrickIndex++;

        return true;
    }

    public Hand getTurn() {
        Trick trick = currentTrick();
        if (trick.isOpen())
            return trick.getTurn();
        else
            return trick.determineTrickWinner(getTrump()).get();
    }

    public Set<Card> getDisabledCards() {
        if (currentTrick().isClosed())
            return newHashSet(handCardMultimap.values());

        Set<Card> disabledCards = newHashSet();
        Hand turn = currentTrick().getTurn();
        for (Map.Entry<Hand, Card> handCardEntry : handCardMultimap.entries()) {
            Card card = handCardEntry.getValue();
            if (handCardEntry.getKey() == turn) {
                try {
                    validateHandTurn(turn, card);
                } catch (IllegalSuitException e) {
                    disabledCards.add(card);
                }
            } else {
                disabledCards.add(card);
            }
        }
        return disabledCards;
    }

    public Players getPlayers() {
        return players;
    }

    public Widow getWidow() {
        return widow;
    }

    public boolean isTrickClosed() {
        return currentTrick().isClosed();
    }

    public boolean hasUndoTurns() {
        assert !trickLog.isEmpty() : "trickLog is empty!";
        if (trickLog.size() == 1)
            return trickLog.getFirst().hasUndoTurns();
        return currentTrick().hasRedoTurns() || previousTrick().hasUndoTurns();
    }

    public boolean hasRedoTurns() {
        return trickLog.size() - 1 > currentTrickIndex && currentTrick().hasRedoTurns();
    }

    public void undoTurn() {
        if (hasUndoTurns()) {
            if (currentTrick().isEmpty())
                currentTrickIndex--;

            Turn undoTurn = currentTrick().undoTurn();
            handCardMultimap.put(undoTurn.getHand(), undoTurn.getCard());
        }
    }

    public void redoTurn() {
        if (hasRedoTurns()) {
            if (currentTrick().isClosed())
                currentTrickIndex++;

            Turn redoTurn = currentTrick().redoTurn();
            handCardMultimap.remove(redoTurn.getHand(), redoTurn.getCard());
        }
    }

    private Trick currentTrick() {
        return trickLog.get(currentTrickIndex);
    }

    private Trick previousTrick() {
        return trickLog.get(currentTrickIndex - 1);
    }

}
