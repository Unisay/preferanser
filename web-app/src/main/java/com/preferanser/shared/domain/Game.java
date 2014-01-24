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
import com.preferanser.shared.domain.entity.Deal;
import com.preferanser.shared.domain.exception.*;
import com.preferanser.shared.util.EnumRotator;
import com.preferanser.shared.util.GameUtils;

import java.util.*;

import static com.google.common.base.Preconditions.checkState;
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
    private final LinkedList<Trick> trickLog;
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
        this.handContracts = ImmutableMap.copyOf(handContracts);
        this.handCardMultimap = HashMultimap.create(handCardMultimap);
        trickLog = Lists.newLinkedList();
        trickLog.add(new Trick(players, turnRotator, centerCardHandMap));
        currentTrickIndex = 0;
    }

    public Game(Deal deal) {
        players = deal.getPlayers();
        widow = deal.getWidow();
        handContracts = ImmutableMap.copyOf(deal.getHandContracts());
        handCardMultimap = HashMultimap.create(deal.getHandCards());
        trickLog = constructTricks(deal, getTrump());
        currentTrickIndex = deal.getCurrentTrickIndex();
    }

    private LinkedList<Trick> constructTricks(Deal deal, Optional<Suit> trump) {
        LinkedList<Trick> tricks = Lists.newLinkedList();
        EnumRotator<Hand> turnRotator = new EnumRotator<Hand>(Hand.values(), deal.getFirstTurn());
        if (deal.getPlayers() == Players.THREE)
            turnRotator.setSkipValues(Hand.WIDOW);
        Trick trick = new Trick(deal.getPlayers(), turnRotator);
        tricks.add(trick);
        if (deal.getTurns() != null) {
            for (Turn turn : deal.getTurns()) {
                assert trick.isOpen();
                trick.applyTurn(turn.getHand(), turn.getCard());
                if (trick.isClosed()) {
                    tricks.add(trick = createNewTrick(trick, trump, deal.getPlayers()));
                }
            }
        }
        return tricks;
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
        Map<Hand, Integer> counts = newHashMap();
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

    public void makeTurn(Card card) throws GameException {
        Hand fromHand = getCardHand(card);

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
        truncateTrickLog();
    }

    private Hand getCardHand(Card card) {
        for (Map.Entry<Hand, Card> entry : handCardMultimap.entries()) {
            if (entry.getValue() == card)
                return entry.getKey();
        }
        throw new IllegalArgumentException("No hand holds " + card);
    }

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
        Trick currentTrick = currentTrick();
        if (currentTrick.isOpen())
            return false;

        trickLog.add(createNewTrick(currentTrick, getTrump(), players));
        currentTrickIndex++;

        return true;
    }

    private Trick createNewTrick(Trick currentTrick, Optional<Suit> trump, Players gamePlayers) {
        Optional<Hand> trickWinner = currentTrick.determineTrickWinner(trump);
        assert trickWinner.isPresent() : "Trick is closed but winner is not present";
        return new Trick(gamePlayers, new EnumRotator<Hand>(currentTrick.getTurnRotator(), trickWinner.get()));
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
        if (0 == currentTrickIndex)
            return currentTrick().hasUndoTurns();
        return currentTrick().isEmpty()
            ? previousTrick().hasUndoTurns()
            : currentTrick().hasUndoTurns();
    }

    public boolean hasRedoTurns() {
        int lastTrickIndex = trickLog.size() - 1;
        checkState(lastTrickIndex >= currentTrickIndex, "currentTrickIndex == %s, trickLog.size() == %s", currentTrickIndex, trickLog.size());

        if (lastTrickIndex > currentTrickIndex && currentTrick().isClosed())
            currentTrickIndex++;

        return currentTrick().hasRedoTurns();
    }

    public boolean undoTurn() {
        if (!hasUndoTurns())
            return false;

        if (currentTrick().isEmpty())
            currentTrickIndex--;

        Turn undoTurn = currentTrick().undoTurn();
        handCardMultimap.put(undoTurn.getHand(), undoTurn.getCard());
        return true;
    }

    public boolean redoTurn() {
        if (!hasRedoTurns())
            return false;

        Turn redoTurn = currentTrick().redoTurn();
        handCardMultimap.remove(redoTurn.getHand(), redoTurn.getCard());
        sluffTrick();
        return true;
    }

    private Trick currentTrick() {
        return trickLog.get(currentTrickIndex);
    }

    private Trick previousTrick() {
        return trickLog.get(currentTrickIndex - 1);
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

    public Deal toDeal(String name, String description) {
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
