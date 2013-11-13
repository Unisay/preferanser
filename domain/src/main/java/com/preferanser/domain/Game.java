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

package com.preferanser.domain;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.preferanser.util.EnumRotator;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static com.preferanser.domain.TableLocation.CENTER;

/**
 * Represents game state
 */
public class Game {

    private final int numPlayers;
    private final EnumRotator<Cardinal> turnRotator;
    private final Map<Cardinal, Contract> cardinalContracts;
    private final Multimap<Cardinal, Card> cardinalCardMultimap;
    private final Map<Cardinal, Integer> cardinalTricks = Maps.newHashMapWithExpectedSize(Cardinal.values().length);
    private final Map<Card, Cardinal> centerCardCardinalMap = Maps.newLinkedHashMap(); // order is important

    Game(int numPlayers,
         Map<Cardinal, Contract> cardinalContracts,
         EnumRotator<Cardinal> turnRotator,
         Multimap<Cardinal, Card> cardinalCardMultimap
    ) {
        this.numPlayers = numPlayers;
        this.cardinalContracts = cardinalContracts;
        this.turnRotator = turnRotator;
        this.cardinalCardMultimap = cardinalCardMultimap;
        initCardinalTricks();
    }

    private void initCardinalTricks() {
        for (Cardinal cardinal : Cardinal.values()) {
            cardinalTricks.put(cardinal, 0);
        }
    }

    public Map<TableLocation, Collection<Card>> getCardinalCards() {
        return GameUtils.copyDefensive(cardinalCardMultimap);
    }

    public Collection<Card> getCardsByCardinal(Cardinal cardinal) {
        return ImmutableList.copyOf(cardinalCardMultimap.get(cardinal));
    }

    public Map<Card, Cardinal> getCenterCards() {
        return ImmutableMap.copyOf(centerCardCardinalMap);
    }

    public Map<Cardinal, Integer> getCardinalTricks() {
        return ImmutableMap.copyOf(cardinalTricks);
    }

    public Map<Cardinal, Contract> getCardinalContracts() {
        return ImmutableMap.copyOf(cardinalContracts);
    }

    private Optional<Contract> getPlayingContract() {
        for (Contract contract : cardinalContracts.values()) {
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

    public boolean moveCardToCenter(Card card, TableLocation oldLocation) {
        checkArgument(oldLocation != CENTER, "Game.moveCardToCenter(oldLocation==CENTER)");

        if (centerCardCardinalMap.size() == numPlayers)
            return false;

        Cardinal oldCardinal = GameUtils.tableLocationToCardinal(oldLocation);
        if (centerCardCardinalMap.containsValue(oldCardinal))
            return false;

        cardinalCardMultimap.get(oldCardinal).remove(card);
        centerCardCardinalMap.put(card, oldCardinal);
        return true;
    }

    public boolean moveCenterCardsToSluff() {
        if (centerCardCardinalMap.size() == numPlayers) {
            Optional<Suit> maybeTrump = getTrump();
            Cardinal turn = determineTrickWinner(maybeTrump, centerCardCardinalMap);
            cardinalTricks.put(turn, cardinalTricks.get(turn) + 1); // Non-atomic increment!
            turnRotator.setCurrent(turn);
            centerCardCardinalMap.clear();
            return true;
        }
        return false;
    }

    private Cardinal determineTrickWinner(Optional<Suit> maybeTrump, Map<Card, Cardinal> cardCardinalMap) {
        assert (cardCardinalMap.size() == numPlayers);
        Iterator<Card> cardIterator = cardCardinalMap.keySet().iterator();
        Card maxCard = cardIterator.next();
        while (cardIterator.hasNext()) {
            Card nextCard = cardIterator.next();
            if (maybeTrump.isPresent() && maxCard.getSuit() != maybeTrump.get() && nextCard.getSuit() == maybeTrump.get()) {
                maxCard = nextCard;
            } else if (maxCard.getSuit() == nextCard.getSuit()) {
                if (Rank.comparator().compare(maxCard.getRank(), nextCard.getRank()) < 0) {
                    maxCard = nextCard;
                }
            }
        }
        return cardCardinalMap.get(maxCard);
    }

    public Cardinal getTurn() {
        return turnRotator.current();
    }

    public int getNumPlayers() {
        return numPlayers;
    }


}
