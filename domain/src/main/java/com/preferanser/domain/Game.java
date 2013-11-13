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
import com.google.common.base.Preconditions;
import com.google.common.collect.*;
import com.preferanser.domain.exception.GameBuilderException;
import com.preferanser.util.EnumRotator;

import java.util.*;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Lists.newArrayList;
import static com.preferanser.domain.TableLocation.*;

/**
 * Represents game state
 */
public class Game {

    private static final int NUM_OF_CONTRACTS = 3;

    private final int numPlayers;
    private final EnumRotator<Cardinal> turnRotator;
    private final Map<Cardinal, Contract> cardinalContracts;
    private final LinkedHashMultimap<Cardinal, Card> cardinalCardMultimap;
    private final Map<Cardinal, Integer> cardinalTricks = Maps.newHashMapWithExpectedSize(Cardinal.values().length);
    private final Map<Card, Cardinal> centerCardCardinalMap = Maps.newLinkedHashMap(); // order is important

    private Game(int numPlayers,
                 Map<Cardinal, Contract> cardinalContracts,
                 EnumRotator<Cardinal> turnRotator,
                 LinkedHashMultimap<Cardinal, Card> cardinalCardMultimap
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
        return Builder.copyDefensive(cardinalCardMultimap);
    }

    public Collection<Card> getCardsByCardinal(Cardinal cardinal) {
        return ImmutableList.copyOf(cardinalCardMultimap.get(cardinal));
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

    public boolean moveCardToCenter(Card card, TableLocation oldLocation) {
        checkArgument(oldLocation != CENTER, "Game.moveCardToCenter(oldLocation==CENTER)");

        if (centerCardCardinalMap.size() == numPlayers)
            return false;

        Cardinal oldCardinal = tableLocationToCardinal(oldLocation);
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

    private static Cardinal tableLocationToCardinal(TableLocation location) {
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

    public Cardinal getTurn() {
        return turnRotator.current();
    }

    public int getNumPlayers() {
        return numPlayers;
    }


    public static class Builder {

        private static final int NUM_OF_CARDS_PER_CARDINAL = 10;

        public static enum Error {
            WRONG_FIRST_TURN,
            WRONG_CARDINAL_CARDS,
            FIRST_TURN_NOT_SPECIFIED,
            NUM_PLAYERS_NOT_SPECIFIED,
            HAS_CONFLICTING_CONTRACTS,
            WRONG_NUMBER_OF_CONTRACTS
        }

        private Integer numPlayers;

        private Cardinal firstTurn;

        private Map<Cardinal, Contract> cardinalContracts
                = Maps.newHashMapWithExpectedSize(Cardinal.values().length);

        private LinkedHashMultimap<Cardinal, Card> cardinalCardMultimap
                = LinkedHashMultimap.create(TableLocation.values().length, Card.values().length);

        private Map<Card, Cardinal> centerCardCardinalMap
                = Maps.newLinkedHashMap(); // order is important

        public Builder setFirstTurn(Cardinal firstTurn) {
            this.firstTurn = firstTurn;
            return this;
        }

        public Builder setThreePlayers() {
            numPlayers = 3;
            return this;
        }

        public Builder setFourPlayers() {
            numPlayers = 4;
            return this;
        }

        public Builder setCardinalContract(Cardinal cardinal, Contract contract) {
            Preconditions.checkNotNull(cardinal);
            Preconditions.checkNotNull(contract);
            cardinalContracts.put(cardinal, contract);
            return this;
        }

        public Builder putCards(Cardinal cardinal, Collection<Card> cards) {
            cardinalCardMultimap.putAll(cardinal, cards);
            return this;
        }

        public Builder putCards(Cardinal cardinal, Card... cards) {
            putCards(cardinal, newArrayList(cards));
            return this;
        }

        public Builder clearCards(TableLocation... tableLocations) {
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
            return this;
        }

        public boolean moveCard(Card card, TableLocation oldLocation, TableLocation newLocation) {
            if (CENTER == oldLocation) { // moving card out of center
                checkArgument(centerCardCardinalMap.containsKey(card), "There is no %s in TableLocation.CENTER", card);
                centerCardCardinalMap.remove(card);
                cardinalCardMultimap.get(tableLocationToCardinal(newLocation)).add(card);
            } else if (CENTER == newLocation) { // moving card to center
                if (centerCardCardinalMap.size() == numPlayers) {
                    return false;
                }
                Cardinal oldCardinal = tableLocationToCardinal(oldLocation);
                checkArgument(!centerCardCardinalMap.containsValue(oldCardinal), "There is a card from %s in TableLocation.CENTER", oldCardinal);
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

        private Optional<List<Error>> validate() {
            List<Error> errors = newArrayList();

            if (numPlayers == null)
                errors.add(Error.NUM_PLAYERS_NOT_SPECIFIED);

            if (firstTurn == null)
                errors.add(Error.FIRST_TURN_NOT_SPECIFIED);

            if (wrongNumberOfContracts())
                errors.add(Error.WRONG_NUMBER_OF_CONTRACTS);

            if (wrongFirstTurn())
                errors.add(Error.WRONG_FIRST_TURN);

            if (hasConflictingContracts())
                errors.add(Error.HAS_CONFLICTING_CONTRACTS);

            if (wrongCardinalCards())
                errors.add(Error.WRONG_CARDINAL_CARDS);

            if (errors.isEmpty())
                return Optional.absent();
            else
                return Optional.of(errors);
        }

        private boolean wrongNumberOfContracts() {
            int count = 0;
            for (Map.Entry<Cardinal, Contract> entry : cardinalContracts.entrySet()) {
                if (entry.getValue() != null)
                    count++;
            }
            return count != NUM_OF_CONTRACTS;
        }

        private boolean wrongFirstTurn() {
            return numPlayers != null
                    && numPlayers == 3
                    && firstTurn != null
                    && cardinalContracts.get(firstTurn) == null;
        }

        private boolean hasConflictingContracts() {
            int numOfContracts = 0;
            int numOfPlayingContracts = 0;
            int numOfWhists = 0;
            for (Contract contract : cardinalContracts.values()) {
                if (contract != null) {
                    numOfContracts++;
                    if (contract.isPlaying())
                        numOfPlayingContracts++;
                    if (contract == Contract.WHIST)
                        numOfWhists++;
                }
            }
            return numOfContracts != 0 && (numOfPlayingContracts > 1 || numOfWhists == numOfContracts);
        }

        private boolean wrongCardinalCards() {
            for (Cardinal cardinal : Cardinal.values())
                if (cardinalContracts.get(cardinal) != null && cardinalCardMultimap.get(cardinal).size() != NUM_OF_CARDS_PER_CARDINAL)
                    return true;
            return false;
        }

        public Game build() throws GameBuilderException {
            Optional<List<Error>> validationErrors = validate();
            if (validationErrors.isPresent())
                throw new GameBuilderException(validationErrors.get());

            EnumRotator<Cardinal> cardinalRotator = new EnumRotator<Cardinal>(Cardinal.values(), firstTurn);
            return new Game(
                    numPlayers,
                    cardinalContracts,
                    cardinalRotator,
                    cardinalCardMultimap
            );
        }

        public Cardinal getFirstTurn() {
            return firstTurn;
        }

        public Map<Cardinal, Contract> getCardinalContracts() {
            return cardinalContracts;
        }

        public Map<TableLocation, Collection<Card>> getTableCards() {
            return copyDefensive(cardinalCardMultimap);
        }

        public Map<Card, Cardinal> getCenterCards() {
            return new LinkedHashMap<Card, Cardinal>(centerCardCardinalMap);
        }

        private static Map<TableLocation, Collection<Card>> copyDefensive(Multimap<Cardinal, Card> cardinalCardMultimap) {
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

    }

}
