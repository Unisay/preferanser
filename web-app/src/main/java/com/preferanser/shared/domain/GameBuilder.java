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
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Maps;
import com.preferanser.shared.domain.exception.DuplicateGameTurnException;
import com.preferanser.shared.domain.exception.GameBuilderException;
import com.preferanser.shared.util.EnumRotator;
import com.preferanser.shared.util.GameUtils;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Lists.newArrayList;
import static com.preferanser.shared.domain.TableLocation.CENTER;

public class GameBuilder {

    private static final int NUM_OF_CARDS_PER_CARDINAL = 10;
    private static final int NUM_OF_CONTRACTS = 3;

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

    public GameBuilder setFirstTurn(Cardinal firstTurn) {
        this.firstTurn = firstTurn;
        return this;
    }

    public GameBuilder setThreePlayers() {
        numPlayers = 3;
        return this;
    }

    public GameBuilder setFourPlayers() {
        numPlayers = 4;
        return this;
    }

    public GameBuilder setCardinalContract(Cardinal cardinal, Contract contract) {
        Preconditions.checkNotNull(cardinal);
        Preconditions.checkNotNull(contract);
        cardinalContracts.put(cardinal, contract);
        return this;
    }

    public GameBuilder putCards(Cardinal cardinal, Collection<Card> cards) {
        cardinalCardMultimap.putAll(cardinal, cards);
        return this;
    }

    public GameBuilder putCards(Cardinal cardinal, Card... cards) {
        putCards(cardinal, newArrayList(cards));
        return this;
    }

    public GameBuilder clearCards(TableLocation... tableLocations) {
        if (tableLocations.length == 0) {
            cardinalCardMultimap.clear();
        } else {
            for (TableLocation tableLocation : tableLocations) {
                switch (tableLocation) {
                    case CENTER:
                        centerCardCardinalMap.clear();
                        break;
                    default:
                        cardinalCardMultimap.get(GameUtils.tableLocationToCardinal(tableLocation)).clear();
                }
            }
        }
        return this;
    }

    public boolean moveCard(Card card, TableLocation oldLocation, TableLocation newLocation) throws DuplicateGameTurnException {
        if (CENTER == oldLocation) { // moving card out of center
            checkArgument(centerCardCardinalMap.containsKey(card), "There is no %s in TableLocation.CENTER", card);
            centerCardCardinalMap.remove(card);
            cardinalCardMultimap.get(GameUtils.tableLocationToCardinal(newLocation)).add(card);
        } else if (CENTER == newLocation) { // moving card to center
            if (centerCardCardinalMap.size() == numPlayers) {
                return false;
            }
            Cardinal oldCardinal = GameUtils.tableLocationToCardinal(oldLocation);
            if (centerCardCardinalMap.containsValue(oldCardinal))
                throw new DuplicateGameTurnException(centerCardCardinalMap, oldCardinal);
            cardinalCardMultimap.get(oldCardinal).remove(card);
            centerCardCardinalMap.put(card, oldCardinal);
        } else {
            Cardinal oldCardinal = GameUtils.tableLocationToCardinal(oldLocation);
            Cardinal newCardinal = GameUtils.tableLocationToCardinal(newLocation);
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
        cardinalRotator.setSkipValues(getWidowCardinal());

        return new Game(
            numPlayers,
            cardinalContracts,
            cardinalRotator,
            cardinalCardMultimap,
            centerCardCardinalMap
        );
    }

    private Cardinal getWidowCardinal() {
        for (Cardinal cardinal : Cardinal.values())
            if (!cardinalContracts.containsKey(cardinal))
                return cardinal;

        throw new IllegalStateException("Failed to determine widow cardinal");
    }

    public Cardinal getFirstTurn() {
        return firstTurn;
    }

    public Map<Cardinal, Contract> getCardinalContracts() {
        return cardinalContracts;
    }

    public Map<TableLocation, Collection<Card>> getTableCards() {
        return GameUtils.copyDefensive(cardinalCardMultimap);
    }

    public Map<Card, Cardinal> getCenterCards() {
        return new LinkedHashMap<Card, Cardinal>(centerCardCardinalMap);
    }


}
