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
import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.preferanser.shared.domain.exception.*;
import com.preferanser.shared.util.EnumRotator;
import com.preferanser.shared.util.GameUtils;

import java.util.*;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;

/**
 * Represents game player
 */
public class Player {

    private Long id;
    private final String name;
    private final String description;
    private final Players players;
    private final Widow widow;
    private final Map<Hand, Contract> handContracts;
    private final Multimap<Hand, Card> handCardMultimap;
    private final LinkedList<Trick> trickLog;
    private int currentTrickIndex;

    public Player(Deal deal) {
        id = deal.getId();
        name = deal.getName();
        description = deal.getDescription();
        players = deal.getPlayers();
        widow = deal.getWidow();
        handContracts = ImmutableMap.copyOf(deal.getHandContracts());
        handCardMultimap = HashMultimap.create(deal.getHandCards());
        currentTrickIndex = deal.getCurrentTrickIndex();
        TurnRotator turnRotator = constructTurnRotator(deal.getFirstTurn());
        trickLog = Lists.newLinkedList(Arrays.asList(new Trick(players, turnRotator)));
        populateTrickLog(deal.getPlayers(), deal.getTurns(), getTrump());
    }

    private TurnRotator constructTurnRotator(Hand firstTurn) {
        Preconditions.checkArgument(firstTurn != Hand.WIDOW, "First turn from WIDOW is not allowed");
        EnumRotator<Hand> enumRotator = new EnumRotator<Hand>(Hand.values(), firstTurn, Hand.WIDOW);
        TurnRotator turnRotator = new EnumTurnRotator(enumRotator);
        if (isRaspass()) {
            turnRotator = new RaspassTurnRotator(turnRotator);
        }
        return turnRotator;
    }

    private void populateTrickLog(Players players, List<Turn> turns, Optional<Suit> trump) {
        Preconditions.checkNotNull(turns, "List of turns is null");
        Trick trick = trickLog.getFirst();
        for (Turn turn : turns) {
            trick.applyTurn(turn.getHand(), turn.getCard());
            assert !trickLog.isEmpty();
            if (isTrickClosed(trickLog.size() - 1)) {
                trick = createNewTrick(trick, trump, players);
                trickLog.add(trick);
            }
        }
    }

    public Map<Hand, Set<Card>> getHandCards() {
        return GameUtils.copyDefensive(handCardMultimap);
    }

    public Map<Card, Hand> getCenterCards() {
        return currentTrick().asMap();
    }

    public Map<Hand, Integer> getHandTrickCounts() {
        Map<Hand, Integer> counts = newHashMap();
        for (Hand hand : Hand.values())
            counts.put(hand, getHandTricksCount(hand));
        return counts;
    }

    public int getHandTricksCount(Hand hand) {
        int count = 0;
        Optional<Suit> trump = getTrump();
        for (int trickIndex = 0; trickIndex <= currentTrickIndex; trickIndex++) {
            if (isTrickClosed(trickIndex)) {
                Optional<Hand> maybeWinner = trickLog.get(trickIndex).determineTrickWinner(trump);
                if (maybeWinner.isPresent() && maybeWinner.get() == hand) {
                    count++;
                }
            }
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

    public void makeTurn(Card card) throws GameException {
        Hand fromHand = getCardHand(card);

        if (fromHand != currentTrick().getTurn())
            throw new NotInTurnException(currentTrick().getTurn(), fromHand, card);

        if (currentTrick().hasCardFrom(fromHand))
            throw new DuplicateGameTurnException(currentTrick().asMap(), fromHand);

        if (isTrickClosed())
            throw new NoTurnsAllowedException(currentTrick().asMap());

        validateHandTurn(fromHand, card);

        boolean removed = handCardMultimap.get(fromHand).remove(card);
        if (!removed)
            removed = widow.remove(card);
        assert removed : "Failed to remove " + card + " from " + fromHand;
        currentTrick().applyTurn(fromHand, card);
        truncateTrickLog();
    }

    // TODO unit-test
    public void makeTurnFromWidow() throws GameException {
        makeTurn(widow.getFirstCard());
    }

    private Hand getCardHand(Card card) {
        if (widow.containsCard(card))
            return Hand.WIDOW;
        for (Map.Entry<Hand, Card> entry : handCardMultimap.entries()) {
            if (entry.getValue() == card)
                return entry.getKey();
        }
        throw new IllegalArgumentException("No hand holds " + card + ": " + handCardMultimap);
    }

    /**
     * Removes all subsequent turns which might have been left after undo actions
     */
    private void truncateTrickLog() {
        trickLog.subList(currentTrickIndex + 1, trickLog.size()).clear();
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
        if (isTrickOpen(currentTrickIndex))
            return false;

        trickLog.add(createNewTrick(currentTrick(), getTrump(), players));
        currentTrickIndex++;

        return true;
    }

    private Trick createNewTrick(Trick currentTrick, Optional<Suit> trump, Players gamePlayers) {
        Optional<Hand> trickWinner = currentTrick.determineTrickWinner(trump);
        assert trickWinner.isPresent() : "Trick is closed but winner is not present";
        TurnRotator turnRotator = currentTrick.getTurnRotator().produceNextRotator(trickWinner.get());
        return new Trick(gamePlayers, turnRotator);
    }

    public Hand getTurn() {
        Trick currentTrick = currentTrick();
        if (isTrickOpen(currentTrickIndex)) {
            return currentTrick.getTurn();
        } else {
            Optional<Hand> optionalTrickWinner = currentTrick.determineTrickWinner(getTrump());
            assert optionalTrickWinner.isPresent() : "Trick is closed but winner is absent";
            Hand winnerHand = optionalTrickWinner.get();
            return winnerHand == Hand.WIDOW ? currentTrick.getTurn() : winnerHand;
        }
    }

    public List<Card> getTurns() {
        List<Card> turns = newArrayList();
        for (Trick trick : trickLog) {
            for (Turn turn : trick) {
                turns.add(turn.getCard());
            }
        }
        return turns;
    }

    public Set<Card> getDisabledCards() {
        if (isTrickClosed())
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
        return isTrickClosed(currentTrickIndex);
    }

    private boolean isTrickClosed(int currentTrickIndex) {
        return !isTrickOpen(currentTrickIndex);
    }

    private boolean isTrickOpen(int trickIndex) {
        Trick trick = trickLog.get(trickIndex);
        int numTurns = trick.getTurnCount();
        int numPlayers;
        if (isRaspass()) {
            if (trickIndex < 2) {
                numPlayers = Players.FOUR.getNumPlayers();
            } else {
                numPlayers = Players.THREE.getNumPlayers();
            }
        } else {
            numPlayers = players.getNumPlayers();
        }
        checkState(numTurns <= numPlayers, "Invalid trick: " + this);
        return numTurns < numPlayers;
    }

    public boolean hasUndoTurns() {
        assert !trickLog.isEmpty() : "trickLog is empty!";
        if (0 == currentTrickIndex)
            return currentTrick().hasUndoTurns();
        return currentTrick().isEmpty()
            ? previousTrick().hasUndoTurns()
            : currentTrick().hasUndoTurns();
    }

    public boolean hasRedoTurns() {
        int lastTrickIndex = trickLog.size() - 1;
        checkState(lastTrickIndex >= currentTrickIndex, "currentTrickIndex == %s, trickLog.size() == %s", currentTrickIndex, trickLog.size());

        if (lastTrickIndex > currentTrickIndex && isTrickClosed(currentTrickIndex))
            currentTrickIndex++; // TODO unit test

        return currentTrick().hasRedoTurns();
    }

    public boolean undoTurn() {
        if (!hasUndoTurns())
            return false;

        if (currentTrick().isEmpty())
            currentTrickIndex--;

        Turn undoTurn = currentTrick().undoTurn();
        Hand hand = undoTurn.getHand();
        if (hand == Hand.WIDOW) {
            widow.add(undoTurn.getCard()); // TODO unit test
        } else {
            handCardMultimap.put(hand, undoTurn.getCard());
        }
        return true;
    }

    public boolean redoTurn() {
        if (!hasRedoTurns())
            return false; // TODO unit test

        Turn redoTurn = currentTrick().redoTurn();
        Hand hand = redoTurn.getHand();
        if (hand == Hand.WIDOW) {
            widow.remove(redoTurn.getCard()); // TODO unit test
        } else {
            handCardMultimap.remove(hand, redoTurn.getCard());
        }
        sluffTrick();
        return true;
    }

    private Trick currentTrick() {
        return trickLog.get(currentTrickIndex);
    }

    private Trick previousTrick() {
        return trickLog.get(currentTrickIndex - 1);
    }

    private boolean isRaspass() {
        return handContracts.get(Hand.EAST) == Contract.PASS
            && handContracts.get(Hand.SOUTH) == Contract.PASS
            && handContracts.get(Hand.WEST) == Contract.PASS;
    }

    public void reset() {
        boolean turnUndone;
        do {
            turnUndone = undoTurn();
        } while (turnUndone);
        Trick first = trickLog.getFirst();
        first.clearTurnLog();
        trickLog.clear();
        trickLog.add(first);
    }

    public void loadDrawing(Drawing drawing) throws GameException {
        reset();
        for (Card card : drawing.getTurns()) {
            makeTurn(card);
            sluffTrick();
        }
        for (Card ignored : drawing.getTurns()) {
            undoTurn();
        }
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean hasTurns() {
        return hasRedoTurns() || hasUndoTurns();
    }

    public Deal toDeal() {
        Deal deal = new Deal();
        deal.setName(name);
        deal.setDescription(description);
        deal.setCreated(new Date());
        deal.setFirstTurn(trickLog.getFirst().getTurn());
        deal.setPlayers(players);
        deal.setWidow(widow);
        deal.setEastContract(handContracts.get(Hand.EAST));
        deal.setSouthContract(handContracts.get(Hand.SOUTH));
        deal.setWestContract(handContracts.get(Hand.WEST));
        deal.setEastCards(newHashSet(handCardMultimap.get(Hand.EAST)));
        deal.setSouthCards(newHashSet(handCardMultimap.get(Hand.SOUTH)));
        deal.setWestCards(newHashSet(handCardMultimap.get(Hand.WEST)));
        List<Turn> turns = Lists.newArrayListWithCapacity(trickLog.size());
        for (Trick trick : trickLog) for (Turn turn : trick) turns.add(turn);
        deal.setTurns(turns);
        deal.setCurrentTrickIndex(currentTrickIndex);
        return deal;
    }

}
