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
import com.google.common.base.Preconditions;
import com.google.common.collect.*;

import java.util.*;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Lists.newArrayList;
import static com.preferanser.shared.TableLocation.*;

/**
 * Represents game state
 */
public class Game {

    public static enum Type {
        THREE_PLAYERS(3), FOUR_PLAYERS(4);

        private int numPlayers;

        private Type(int numPlayers) {
            this.numPlayers = numPlayers;
        }
    }

    public static enum Mode {PLAY, EDIT}

    private Multimap<Cardinal, Card> cardinalCardMultimap = LinkedHashMultimap.create(TableLocation.values().length, Card.values().length);
    private Map<Cardinal, Integer> cardinalTricks = Maps.newHashMapWithExpectedSize(Cardinal.values().length);

    private Map<Cardinal, Contract> cardinalContracts = Maps.newHashMapWithExpectedSize(Cardinal.values().length);
    private Map<Card, Cardinal> centerCardCardinalMap = Maps.newLinkedHashMap(); // order is important
    private Type type = Type.THREE_PLAYERS; // TODO selection
    private Mode mode = Mode.EDIT;
    private Cardinal turn;

    public Game() {
        clearCardinalTricks();
    }

    public void putCards(Cardinal cardinal, Collection<Card> cards) {
        cardinalCardMultimap.putAll(cardinal, cards);
    }

    public void putCards(Cardinal cardinal, Card... cards) {
        putCards(cardinal, newArrayList(cards));
    }

    public void clearCardinalTricks() {
        for (Cardinal cardinal : Cardinal.values()) {
            cardinalTricks.put(cardinal, 0);
        }
    }

    public void clearCards(TableLocation... tableLocations) {
        if (tableLocations.length == 0) {
            cardinalCardMultimap.clear();
        } else {
            for (TableLocation tableLocation : tableLocations) {
                switch (tableLocation) {
                    case CENTER:
                        centerCardCardinalMap.clear();
                        break;
                    default:
                        cardinalCardMultimap.get(tableLocationToCardinal(tableLocation)).clear();
                }
            }
        }
    }

    public boolean moveCard(Card card, TableLocation oldLocation, TableLocation newLocation) {
        checkArgument(oldLocation != newLocation, "moveCard(oldLocation == newLocation)");
        switch (mode) {
            case EDIT:
                return moveCardWhenEditing(card, oldLocation, newLocation);
            case PLAY:
                return moveCardWhenPlaying(card, oldLocation, newLocation);
            default:
                throw new IllegalStateException("Unknown game mode: " + mode);
        }
    }

    private boolean moveCardWhenPlaying(Card card, TableLocation oldLocation, TableLocation newLocation) {
        if (CENTER != newLocation)
            return false;

        if (centerCardCardinalMap.size() == type.numPlayers)
            return false;

        Cardinal oldCardinal = tableLocationToCardinal(oldLocation);
        if (centerCardCardinalMap.containsValue(oldCardinal))
            return false;

        cardinalCardMultimap.get(oldCardinal).remove(card);
        centerCardCardinalMap.put(card, oldCardinal);
        return true;
    }

    private boolean moveCardWhenEditing(Card card, TableLocation oldLocation, TableLocation newLocation) {
        if (CENTER == oldLocation) { // moving card out of center
            checkArgument(centerCardCardinalMap.containsKey(card), "There is no %s in TableLocation.CENTER", card);
            centerCardCardinalMap.remove(card);
            cardinalCardMultimap.get(tableLocationToCardinal(newLocation)).add(card);
        } else if (CENTER == newLocation) { // moving card to center
            if (centerCardCardinalMap.size() == type.numPlayers) {
                return false;
            }
            Cardinal oldCardinal = tableLocationToCardinal(oldLocation);
            if (centerCardCardinalMap.containsValue(oldCardinal))
                return false;

            cardinalCardMultimap.get(oldCardinal).remove(card);
            centerCardCardinalMap.put(card, oldCardinal);
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
        Preconditions.checkNotNull(cardinal);
        Preconditions.checkNotNull(contract);
        cardinalContracts.put(cardinal, contract);
    }

    public Map<TableLocation, Collection<Card>> getCardinalCards() {
        ImmutableMap.Builder<TableLocation, Collection<Card>> builder = ImmutableMap.builder();
        if (cardinalCardMultimap.containsKey(Cardinal.NORTH)) {
            builder.put(NORTH, ImmutableList.copyOf(cardinalCardMultimap.get(Cardinal.NORTH)));
        }
        if (cardinalCardMultimap.containsKey(Cardinal.EAST)) {
            builder.put(EAST, ImmutableList.copyOf(cardinalCardMultimap.get(Cardinal.EAST)));
        }
        if (cardinalCardMultimap.containsKey(Cardinal.SOUTH)) {
            builder.put(SOUTH, ImmutableList.copyOf(cardinalCardMultimap.get(Cardinal.SOUTH)));
        }
        if (cardinalCardMultimap.containsKey(Cardinal.WEST)) {
            builder.put(WEST, ImmutableList.copyOf(cardinalCardMultimap.get(Cardinal.WEST)));
        }
        return builder.build();
    }

    public LinkedHashMap<Card, Cardinal> getCenterCards() {
        return new LinkedHashMap<Card, Cardinal>(centerCardCardinalMap);
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
            return contract.getTrump();
        }
        return Optional.absent();
    }

    public boolean moveCenterCardsToSluff() {
        if (mode == Mode.PLAY && centerCardCardinalMap.size() == type.numPlayers) {
            Optional<Suit> maybeTrump = getTrump();
            turn = determineTrickWinner(maybeTrump, centerCardCardinalMap);
            cardinalTricks.put(turn, cardinalTricks.get(turn) + 1); // Non-atomic increment!
            clearCards(CENTER);
            return true;
        }
        return false;
    }

    private Cardinal determineTrickWinner(Optional<Suit> maybeTrump, Map<Card, Cardinal> cardCardinalMap) {
        assert (cardCardinalMap.size() == type.numPlayers);
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

    public void setType(Type type) {
        this.type = type;
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public Cardinal getTurn() {
        return turn;
    }

    public void setTurn(Cardinal turn) {
        this.turn = turn;
    }
}
