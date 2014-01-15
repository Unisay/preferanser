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

import java.util.*;

import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Maps.newLinkedHashMap;
import static com.google.common.collect.Sets.newHashSet;

/**
 * Represents game state
 */
public class Game {

    private final Players players;
    private final Widow widow;
    private final EnumRotator<Hand> turnRotator;
    private final Map<Hand, Contract> handContracts;
    private final Multimap<Hand, Card> handCardMultimap;
    private final Map<Hand, Integer> handTricks = Maps.newHashMapWithExpectedSize(Hand.values().length);
    private final LinkedHashMap<Card, Hand> centerCardHandMap;  // order is important

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
        this.turnRotator = new EnumRotator<Hand>(turnRotator);
        this.handCardMultimap = LinkedHashMultimap.create(handCardMultimap);
        this.centerCardHandMap = newLinkedHashMap(centerCardHandMap);  // order is important
        initHandTricks();
    }

    private void initHandTricks() {
        for (Hand hand : Hand.values()) {
            handTricks.put(hand, 0);
        }
    }

    public Map<Hand, Set<Card>> getHandCards() {
        return GameUtils.copyDefensive(handCardMultimap);
    }

    public Collection<Card> getCardsByHand(Hand hand) {
        return ImmutableList.copyOf(handCardMultimap.get(hand));
    }

    public Map<Card, Hand> getCenterCards() {
        return ImmutableMap.copyOf(centerCardHandMap);
    }

    public Map<Hand, Integer> getHandTricks() {
        return ImmutableMap.copyOf(handTricks);
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
        if (fromHand != turnRotator.current())
            throw new NotInTurnException(turnRotator.current(), fromHand);

        if (!handCardMultimap.containsEntry(fromHand, card))
            throw new NoSuchHandCardException(fromHand, card);

        if (centerCardHandMap.containsValue(fromHand))
            throw new DuplicateGameTurnException(centerCardHandMap, fromHand);

        if (centerCardHandMap.size() == players.getNumPlayers())
            throw new NoTurnsAllowedException(centerCardHandMap);

        validateHandTurn(fromHand, card);

        boolean removed = handCardMultimap.get(fromHand).remove(card);
        assert removed : "Failed to remove " + card + " from " + fromHand;

        centerCardHandMap.put(card, fromHand);
        turnRotator.next();
    }

    // TODO instead of throwing exceptions - return validation errors
    private void validateHandTurn(Hand hand, Card card) throws IllegalSuitException {
        Optional<Suit> maybeTrickSuit = getTrickSuit();
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

    private Optional<Suit> getTrickSuit() {
        if (centerCardHandMap.isEmpty())
            return Optional.absent();
        return Optional.of(centerCardHandMap.entrySet().iterator().next().getKey().getSuit());
    }

    public boolean isTrickComplete() {
        return centerCardHandMap.size() == players.getNumPlayers();
    }

    public boolean sluffTrick() {
        if (!isTrickComplete())
            return false;

        Hand turn = determineTrickWinner();
        handTricks.put(turn, handTricks.get(turn) + 1); // Non-atomic increment!
        turnRotator.setCurrent(turn);
        centerCardHandMap.clear();
        return true;
    }

    private Hand determineTrickWinner() {
        assert (centerCardHandMap.size() == players.getNumPlayers());
        Iterator<Card> cardIterator = centerCardHandMap.keySet().iterator();
        Card maxCard = cardIterator.next();
        Optional<Suit> optionalTrump = getTrump();
        while (cardIterator.hasNext()) {
            Card nextCard = cardIterator.next();
            if (optionalTrump.isPresent() && maxCard.getSuit() != optionalTrump.get() && nextCard.getSuit() == optionalTrump.get()) {
                maxCard = nextCard;
            } else if (maxCard.getSuit() == nextCard.getSuit()) {
                if (Rank.comparator().compare(maxCard.getRank(), nextCard.getRank()) < 0) {
                    maxCard = nextCard;
                }
            }
        }
        return centerCardHandMap.get(maxCard);
    }

    public Hand getTurn() {
        if (isTrickComplete())
            return determineTrickWinner();
        else
            return turnRotator.current();
    }

    public Set<Card> getDisabledCards() {
        if (isTrickComplete())
            return newHashSet(handCardMultimap.values());
        Set<Card> disabledCards = newHashSet();
        Hand turn = turnRotator.current();
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

}
