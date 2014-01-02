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
import com.preferanser.shared.domain.entity.Deal;
import com.preferanser.shared.domain.exception.DuplicateGameTurnException;
import com.preferanser.shared.domain.exception.GameBuilderException;
import com.preferanser.shared.util.Clock;
import com.preferanser.shared.util.EnumRotator;
import com.preferanser.shared.util.GameUtils;

import java.util.*;

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

    private GamePlayers gamePlayers;

    private Cardinal firstTurn;

    private Map<Cardinal, Contract> cardinalContracts
        = Maps.newHashMapWithExpectedSize(Cardinal.values().length);

    private LinkedHashMultimap<Cardinal, Card> cardinalCardMultimap
        = LinkedHashMultimap.create(TableLocation.values().length, Card.values().length);

    private Map<Card, Cardinal> centerCardCardinalMap
        = Maps.newLinkedHashMap(); // order is important

    public GameBuilder setDeal(Deal deal) {
        firstTurn = deal.getFirstTurn();
        gamePlayers = deal.getGamePlayers();
        setCardinalDealContracts(deal);
        setCardinalDealCards(deal);
        setCenterDealCards(deal);
        return this;
    }

    private void setCardinalDealContracts(Deal deal) {
        if (deal.getNorthContract() == null)
            cardinalContracts.remove(Cardinal.NORTH);
        else
            cardinalContracts.put(Cardinal.NORTH, deal.getNorthContract());

        if (deal.getEastContract() == null)
            cardinalContracts.remove(Cardinal.EAST);
        else
            cardinalContracts.put(Cardinal.EAST, deal.getEastContract());

        if (deal.getSouthContract() == null)
            cardinalContracts.remove(Cardinal.SOUTH);
        else
            cardinalContracts.put(Cardinal.SOUTH, deal.getSouthContract());

        if (deal.getWestContract() == null)
            cardinalContracts.remove(Cardinal.WEST);
        else
            cardinalContracts.put(Cardinal.WEST, deal.getWestContract());
    }

    private void setCardinalDealCards(Deal deal) {
        cardinalCardMultimap.putAll(Cardinal.NORTH, deal.getNorthCards());
        cardinalCardMultimap.putAll(Cardinal.EAST, deal.getEastCards());
        cardinalCardMultimap.putAll(Cardinal.SOUTH, deal.getSouthCards());
        cardinalCardMultimap.putAll(Cardinal.WEST, deal.getWestCards());
    }

    private void setCenterDealCards(Deal deal) {
        if (deal.getCenterNorthCard() == null)
            removeCenterCardByCardinal(Cardinal.NORTH);
        else
            centerCardCardinalMap.put(deal.getCenterNorthCard(), Cardinal.NORTH);

        if (deal.getCenterEastCard() == null)
            removeCenterCardByCardinal(Cardinal.EAST);
        else
            centerCardCardinalMap.put(deal.getCenterEastCard(), Cardinal.EAST);

        if (deal.getCenterSouthCard() == null)
            removeCenterCardByCardinal(Cardinal.SOUTH);
        else
            centerCardCardinalMap.put(deal.getCenterSouthCard(), Cardinal.SOUTH);

        if (deal.getCenterWestCard() == null)
            removeCenterCardByCardinal(Cardinal.WEST);
        else
            centerCardCardinalMap.put(deal.getCenterWestCard(), Cardinal.WEST);
    }

    private void removeCenterCardByCardinal(Cardinal cardinal) {
        for (Map.Entry<Card, Cardinal> entry : centerCardCardinalMap.entrySet()) {
            if (entry.getValue() == cardinal) {
                centerCardCardinalMap.remove(entry.getKey());
                return;
            }
        }
    }

    public GameBuilder setFirstTurn(Cardinal firstTurn) {
        this.firstTurn = firstTurn;
        return this;
    }

    public GameBuilder setThreePlayers() {
        gamePlayers = GamePlayers.THREE;
        return this;
    }

    public GameBuilder setFourPlayers() {
        gamePlayers = GamePlayers.FOUR;
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
            if (centerCardCardinalMap.size() == gamePlayers.getNumPlayers()) {
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

        if (gamePlayers == null)
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
        return gamePlayers != null
            && gamePlayers == GamePlayers.THREE
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
            gamePlayers,
            cardinalContracts,
            cardinalRotator,
            cardinalCardMultimap,
            centerCardCardinalMap
        );
    }

    public Deal buildDeal(String name) {
        Deal deal = new Deal();
        deal.setCreated(Clock.getNow());
        deal.setFirstTurn(firstTurn);
        deal.setName(name);
        deal.setGamePlayers(gamePlayers);
        initContracts(deal);
        initCardinalCards(deal);
        initCenterCards(deal);
        return deal;
    }

    private void initCenterCards(Deal dto) {
        Map<Card, Cardinal> centerCards = getCenterCards();
        for (Map.Entry<Card, Cardinal> cardCardinalEntry : centerCards.entrySet()) {
            switch (cardCardinalEntry.getValue()) {
                case NORTH:
                    dto.setCenterNorthCard(cardCardinalEntry.getKey());
                    break;
                case EAST:
                    dto.setCenterEastCard(cardCardinalEntry.getKey());
                    break;
                case SOUTH:
                    dto.setCenterSouthCard(cardCardinalEntry.getKey());
                    break;
                case WEST:
                    dto.setCenterWestCard(cardCardinalEntry.getKey());
                    break;
                default:
                    throw new IllegalStateException("Invalid Cardinal constant: " + cardCardinalEntry.getValue());
            }
        }
    }

    private void initCardinalCards(Deal dto) {
        Map<TableLocation, Collection<Card>> tableCards = getTableCards();
        dto.setNorthCards(getCardinalCards(tableCards, TableLocation.NORTH));
        dto.setEastCards(getCardinalCards(tableCards, TableLocation.EAST));
        dto.setSouthCards(getCardinalCards(tableCards, TableLocation.SOUTH));
        dto.setWestCards(getCardinalCards(tableCards, TableLocation.WEST));
    }

    private void initContracts(Deal dto) {
        Map<Cardinal, Contract> cardinalContracts = getCardinalContracts();
        dto.setNorthContract(cardinalContracts.get(Cardinal.NORTH));
        dto.setEastContract(cardinalContracts.get(Cardinal.EAST));
        dto.setSouthContract(cardinalContracts.get(Cardinal.SOUTH));
        dto.setWestContract(cardinalContracts.get(Cardinal.WEST));
    }

    private List<Card> getCardinalCards(Map<TableLocation, Collection<Card>> tableCards, TableLocation location) {
        Collection<Card> cardsCollection = tableCards.get(location);
        List<Card> cards = newArrayList();
        if (cardsCollection == null) {
            cards = Collections.emptyList();
        } else {
            cards.addAll(cardsCollection);
        }
        return cards;
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
