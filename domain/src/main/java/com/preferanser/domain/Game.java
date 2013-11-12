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
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Maps;
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
        ImmutableMap.Builder<TableLocation, Collection<Card>> builder = ImmutableMap.builder();
        if (cardinalCardMultimap.containsKey(Cardinal.NORTH)) {
            builder.put(NORTH, getCardsByCardinal(Cardinal.NORTH));
        }
        if (cardinalCardMultimap.containsKey(Cardinal.EAST)) {
            builder.put(EAST, getCardsByCardinal(Cardinal.EAST));
        }
        if (cardinalCardMultimap.containsKey(Cardinal.SOUTH)) {
            builder.put(SOUTH, getCardsByCardinal(Cardinal.SOUTH));
        }
        if (cardinalCardMultimap.containsKey(Cardinal.WEST)) {
            builder.put(WEST, getCardsByCardinal(Cardinal.WEST));
        }
        return builder.build();
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

        public static enum BuilderError {
            FIRST_TURN_NOT_SPECIFIED,
            HAS_CONFLICTING_CONTRACTS,
            WRONG_CARDINAL_CARDS,
            WRONG_FIRST_TURN, NUM_PLAYERS_NO_SPECIFIED, WRONG_NUMBER_OF_CONTRACTS
        }

        private Integer numPlayers;

        private Cardinal firstTurn;

        private Map<Cardinal, Contract> cardinalContracts
                = Maps.newHashMapWithExpectedSize(Cardinal.values().length);

        private LinkedHashMultimap<Cardinal, Card> cardinalCardMultimap
                = LinkedHashMultimap.create(TableLocation.values().length, Card.values().length);

        private Map<Card, Cardinal> centerCardCardinalMap
                = Maps.newLinkedHashMap(); // order is important

        public Builder firstTurn(Cardinal firstTurn) {
            this.firstTurn = firstTurn;
            return this;
        }

        public Builder threePlayerGame() {
            numPlayers = 3;
            return this;
        }

        public Builder fourPlayerGame() {
            numPlayers = 4;
            return this;
        }

        public Builder cardinalContract(Cardinal cardinal, Contract contract) {
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
            checkArgument(oldLocation != newLocation, "moveCard(oldLocation == newLocation)");
            if (CENTER != newLocation)
                return false;

            if (centerCardCardinalMap.size() == numPlayers)
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
                if (centerCardCardinalMap.size() == numPlayers) {
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

        private Optional<List<BuilderError>> getValidationErrors() {
            List<BuilderError> errors = newArrayList();

            if (numPlayers == null)
                errors.add(BuilderError.NUM_PLAYERS_NO_SPECIFIED);

            if (firstTurn == null)
                errors.add(BuilderError.FIRST_TURN_NOT_SPECIFIED);

            if (wrongNumberOfContracts())
                errors.add(BuilderError.WRONG_NUMBER_OF_CONTRACTS);

            if (wrongFirstTurn())
                errors.add(BuilderError.WRONG_FIRST_TURN);

            if (hasConflictingContracts())
                errors.add(BuilderError.HAS_CONFLICTING_CONTRACTS);

            if (wrongCardinalCards())
                errors.add(BuilderError.WRONG_CARDINAL_CARDS);

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
            return 3 == numPlayers && firstTurn != null && cardinalContracts.get(firstTurn) == null;
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
            Optional<List<BuilderError>> validationErrors = getValidationErrors();
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

    }

}
