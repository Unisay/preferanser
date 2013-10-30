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

package com.preferanser.shared;

import com.google.common.base.Optional;
import com.google.common.collect.*;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Lists.newArrayList;
import static com.preferanser.shared.TableLocation.*;

/**
 * Represents game state
 */
public class Game {

    private enum Type {
        THREE_PLAYERS(3),
        FOUR_PLAYERS(4);

        private int numPlayers;

        private Type(int numPlayers) {
            this.numPlayers = numPlayers;
        }
    }

    private Multimap<Cardinal, Card> cardinalCardMultimap = HashMultimap.create(TableLocation.values().length, Card.values().length);
    private Map<Cardinal, Integer> cardinalTricks = Maps.newHashMapWithExpectedSize(Cardinal.values().length);
    private Map<Cardinal, Contract> cardinalContracts = Maps.newHashMapWithExpectedSize(Cardinal.values().length);
    private BiMap<Card, Cardinal> centerCardCardinalBiMap = EnumBiMap.create(Card.class, Cardinal.class);

    private Type type = Type.THREE_PLAYERS; // TODO selection

    public Game() {
        for (Cardinal cardinal : Cardinal.values()) {
            cardinalTricks.put(cardinal, 0);
        }
    }

    public void putCards(Cardinal cardinal, Collection<Card> cards) {
        cardinalCardMultimap.putAll(cardinal, cards);
    }

    public void putCards(Cardinal cardinal, Card... cards) {
        putCards(cardinal, newArrayList(cards));
    }

    public void clearCards(TableLocation... tableLocations) {
        if (tableLocations.length == 0) {
            cardinalCardMultimap.clear();
        } else {
            for (TableLocation tableLocation : tableLocations) {
                switch (tableLocation) {
                    case CENTER:
                        centerCardCardinalBiMap.clear();
                        break;
                    default:
                        cardinalCardMultimap.get(tableLocationToCardinal(tableLocation)).clear();
                }
            }
        }
    }

    public boolean moveCard(Card card, TableLocation oldLocation, TableLocation newLocation) {
        checkArgument(oldLocation != newLocation, "moveCard(oldLocation == newLocation)");
        if (CENTER == oldLocation) { // moving card out of center
            checkArgument(centerCardCardinalBiMap.containsKey(card), "There is no %s in TableLocation.CENTER", card);
            centerCardCardinalBiMap.remove(card);
            cardinalCardMultimap.get(tableLocationToCardinal(newLocation)).add(card);
        } else if (CENTER == newLocation) { // moving card to center
            if (centerCardCardinalBiMap.size() == type.numPlayers) {
                return false;
            }
            Cardinal oldCardinal = tableLocationToCardinal(oldLocation);
            Cardinal newCardinal = tableLocationToCardinal(newLocation);
            cardinalCardMultimap.get(oldCardinal).remove(card);
            centerCardCardinalBiMap.put(card, newCardinal);
        } else {
            Cardinal oldCardinal = tableLocationToCardinal(oldLocation);
            Cardinal newCardinal = tableLocationToCardinal(newLocation);
            checkArgument(cardinalCardMultimap.get(oldCardinal).contains(card), "There is no %s in Cardinal.%s", card, oldCardinal);
            cardinalCardMultimap.get(oldCardinal).remove(card);
            cardinalCardMultimap.get(newCardinal).add(card);
        }
        return true;
    }

    public void setCardinalContract(Cardinal cardinal, Contract contract) {
        cardinalContracts.put(cardinal, contract);
    }

    public Map<TableLocation, Collection<Card>> getTableCards() {
        return ImmutableMap.<TableLocation, Collection<Card>>builder()
                .put(NORTH, cardinalCardMultimap.get(Cardinal.NORTH))
                .put(EAST, cardinalCardMultimap.get(Cardinal.EAST))
                .put(SOUTH, cardinalCardMultimap.get(Cardinal.SOUTH))
                .put(WEST, cardinalCardMultimap.get(Cardinal.WEST))
                .put(CENTER, centerCardCardinalBiMap.keySet())
                .build();
    }

    public Map<Cardinal, Integer> getCardinalTricks() {
        return Collections.unmodifiableMap(cardinalTricks);
    }

    public Map<Cardinal, Contract> getCardinalContracts() {
        return Collections.unmodifiableMap(cardinalContracts);
    }

    public Optional<Contract> getPlayingContract() {
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
            assert contract.getTrump().isPresent();
            return contract.getTrump();
        }
        return Optional.absent();
    }

    public boolean moveCenterCardsToSluff() {
        Optional<Suit> maybeTrump = getTrump();
        if (maybeTrump.isPresent() && centerCardCardinalBiMap.size() == type.numPlayers) {
            Cardinal winner = determineTrickWinner(maybeTrump.get(), centerCardCardinalBiMap);
            cardinalTricks.put(winner, cardinalTricks.get(winner) + 1); // Non-atomic increment!
            clearCards(CENTER);
            return true;
        }
        return false;
    }

    private Cardinal determineTrickWinner(Suit trump, Map<Card, Cardinal> cardCardinalMap) {
        assert (cardCardinalMap.size() == type.numPlayers);
        Iterator<Card> cardIterator = cardCardinalMap.keySet().iterator();
        Card maxCard = cardIterator.next();
        while (cardIterator.hasNext()) {
            Card nextCard = cardIterator.next();
            if (maxCard.getSuit() != trump && nextCard.getSuit() == trump) {
                maxCard = nextCard;
            } else if (maxCard.getSuit() == nextCard.getSuit()) {
                if (Rank.comparator().compare(maxCard.getRank(), nextCard.getRank()) == -1) {
                    maxCard = nextCard;
                }
            }
        }
        return cardCardinalMap.get(maxCard);
    }

    private Cardinal tableLocationToCardinal(TableLocation location) {
        switch (location) {
            case NORTH:
                return Cardinal.NORTH;
            case EAST:
                return Cardinal.EAST;
            case SOUTH:
                return Cardinal.SOUTH;
            case WEST:
                return Cardinal.WEST;
            default:
                throw new IllegalArgumentException("TableLocation " + location + " doesn't have a corresponding Cardinal!");
        }
    }

}
