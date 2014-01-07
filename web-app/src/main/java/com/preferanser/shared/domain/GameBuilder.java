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
import com.google.common.collect.Sets;
import com.preferanser.shared.domain.entity.Deal;
import com.preferanser.shared.domain.exception.DuplicateGameTurnException;
import com.preferanser.shared.domain.exception.GameBuilderException;
import com.preferanser.shared.domain.exception.validation.*;
import com.preferanser.shared.util.Clock;
import com.preferanser.shared.util.EnumRotator;
import com.preferanser.shared.util.GameUtils;

import java.util.*;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Lists.newArrayList;
import static com.preferanser.shared.domain.TableLocation.CENTER;

public class GameBuilder {

    private static final int NUM_OF_CARDS_PER_HAND = 10;
    private static final int NUM_OF_CONTRACTS = 3;
    private Widow widow;

    private Players players;

    private Hand firstTurn;

    private Map<Hand, Contract> handContracts
        = Maps.newHashMapWithExpectedSize(Hand.PLAYING_HANDS.size());

    private LinkedHashMultimap<Hand, Card> handCardMultimap
        = LinkedHashMultimap.create(TableLocation.values().length, Card.values().length);

    private Map<Card, Hand> centerCardHandMap
        = Maps.newLinkedHashMap(); // order is important

    public GameBuilder setDeal(Deal deal) {
        firstTurn = deal.getFirstTurn();
        players = deal.getPlayers();
        widow = deal.getWidow();
        setHandDealContracts(deal);
        setHandDealCards(deal);
        setCenterDealCards(deal);
        return this;
    }

    private void setHandDealContracts(Deal deal) {
        handContracts.clear();

        if (deal.getEastContract() != null)
            handContracts.put(Hand.EAST, deal.getEastContract());

        if (deal.getSouthContract() != null)
            handContracts.put(Hand.SOUTH, deal.getSouthContract());

        if (deal.getWestContract() != null)
            handContracts.put(Hand.WEST, deal.getWestContract());
    }

    private void setHandDealCards(Deal deal) {
        clearCards();
        putCards(Hand.EAST, deal.getEastCards());
        putCards(Hand.SOUTH, deal.getSouthCards());
        putCards(Hand.WEST, deal.getWestCards());
    }

    private void setCenterDealCards(Deal deal) {
        centerCardHandMap.clear();

        if (deal.getCenterEastCard() != null)
            centerCardHandMap.put(deal.getCenterEastCard(), Hand.EAST);

        if (deal.getCenterSouthCard() != null)
            centerCardHandMap.put(deal.getCenterSouthCard(), Hand.SOUTH);

        if (deal.getCenterWestCard() != null)
            centerCardHandMap.put(deal.getCenterWestCard(), Hand.WEST);
    }

    public GameBuilder setFirstTurn(Hand firstTurn) {
        this.firstTurn = firstTurn;
        return this;
    }

    public GameBuilder setThreePlayers() {
        players = Players.THREE;
        return this;
    }

    public GameBuilder setFourPlayers() {
        players = Players.FOUR;
        return this;
    }

    // TODO: use widow in ui
    public GameBuilder setWidow(Widow widow) {
        this.widow = widow;
        return this;
    }

    public GameBuilder setHandContract(Hand hand, Contract contract) {
        Preconditions.checkNotNull(hand);
        Preconditions.checkNotNull(contract);
        handContracts.put(hand, contract);
        return this;
    }

    public GameBuilder putCards(Hand hand, Collection<Card> cards) {
        handCardMultimap.putAll(hand, cards);
        return this;
    }

    public GameBuilder putCards(Hand hand, Card... cards) {
        putCards(hand, newArrayList(cards));
        return this;
    }

    public GameBuilder clearCards(TableLocation... tableLocations) {
        if (tableLocations.length == 0) {
            handCardMultimap.clear();
        } else {
            for (TableLocation tableLocation : tableLocations) {
                switch (tableLocation) {
                    case CENTER:
                        centerCardHandMap.clear();
                        break;
                    default:
                        handCardMultimap.get(Hand.valueOf(tableLocation)).clear();
                }
            }
        }
        return this;
    }

    public boolean moveCard(Card card, TableLocation oldLocation, TableLocation newLocation) throws DuplicateGameTurnException {
        if (CENTER == oldLocation) { // moving card out of center
            checkArgument(centerCardHandMap.containsKey(card), "There is no %s in TableLocation.CENTER", card);
            centerCardHandMap.remove(card);
            handCardMultimap.get(Hand.valueOf(newLocation)).add(card);
        } else if (CENTER == newLocation) { // moving card to center
            if (centerCardHandMap.size() == players.getNumPlayers()) {
                return false;
            }
            Hand oldHand = Hand.valueOf(oldLocation);
            if (centerCardHandMap.containsValue(oldHand))
                throw new DuplicateGameTurnException(centerCardHandMap, oldHand);
            handCardMultimap.get(oldHand).remove(card);
            centerCardHandMap.put(card, oldHand);
        } else {
            Hand oldHand = Hand.valueOf(oldLocation);
            Hand newHand = Hand.valueOf(newLocation);
            checkArgument(handCardMultimap.get(oldHand).contains(card), "There is no %s in Hand.%s", card, oldHand);
            handCardMultimap.get(oldHand).remove(card);
            handCardMultimap.get(newHand).add(card);
        }
        return true;
    }

    private Optional<List<GameBuilderValidationError>> validate() {
        List<GameBuilderValidationError> errors = newArrayList();

        if (players == null)
            errors.add(new NumPlayersNotSpecifiedValidationError());

        if (firstTurn == null)
            errors.add(new FirstTurnNotSpecifiedValidationError());

        if (wrongNumberOfContracts())
            errors.add(new WrongNumberOfContractsValidationError());

        if (wrongFirstTurn())
            errors.add(new WrongFirstTurnValidationError());

        if (hasConflictingContracts())
            errors.add(new HasConflictingContractsValidationError());

        Set<Card> duplicateCards = findDuplicateCards();
        if (!duplicateCards.isEmpty())
            errors.add(new HasDuplicateCardsValidationError(duplicateCards));

        Map<Hand, Integer> wrongHands = wrongNumberOfCardsPerHand();
        if (!wrongHands.isEmpty())
            errors.add(new WrongNumCardsPerHandValidationError(wrongHands));

        if (errors.isEmpty())
            return Optional.absent();
        else
            return Optional.of(errors);
    }

    private boolean wrongNumberOfContracts() {
        int count = 0;
        for (Map.Entry<Hand, Contract> entry : handContracts.entrySet()) {
            if (entry.getValue() != null)
                count++;
        }
        return count != NUM_OF_CONTRACTS;
    }

    private boolean wrongFirstTurn() {
        return players != null
            && players == Players.THREE
            && firstTurn != null
            && handContracts.get(firstTurn) == null;
    }

    private boolean hasConflictingContracts() {
        int numOfContracts = 0;
        int numOfPlayingContracts = 0;
        int numOfPasses = 0;
        for (Contract contract : handContracts.values()) {
            if (contract != null) {
                numOfContracts++;
                if (contract.isPlaying())
                    numOfPlayingContracts++;
                if (contract == Contract.PASS)
                    numOfPasses++;
            }
        }
        return numOfContracts != 0 && numOfPlayingContracts != 1 && numOfPasses != numOfContracts;
    }

    private Set<Card> findDuplicateCards() {
        Set<Card> cardSet = Sets.newHashSet();
        Set<Card> duplicateCardSet = Sets.newHashSet();
        for (Card card : handCardMultimap.values()) {
            if (!cardSet.add(card)) {
                duplicateCardSet.add(card);
            }
        }
        return duplicateCardSet;
    }

    private Map<Hand, Integer> wrongNumberOfCardsPerHand() {
        Map<Hand, Integer> wrongHands = Maps.newHashMap();
        for (Hand hand : Hand.PLAYING_HANDS) {
            Contract contract = handContracts.get(hand);
            if (contract != null) {
                int numberOfCards = handCardMultimap.get(hand).size();
                if (numberOfCards != NUM_OF_CARDS_PER_HAND)
                    wrongHands.put(hand, numberOfCards);
            }
        }
        return wrongHands;
    }

    public Game build() throws GameBuilderException {
        Optional<List<GameBuilderValidationError>> validationErrors = validate();
        if (validationErrors.isPresent())
            throw new GameBuilderException(validationErrors.get());

        EnumRotator<Hand> handRotator = new EnumRotator<Hand>(Hand.values(), firstTurn);
        handRotator.setSkipValues(Hand.WIDOW);

        return new Game(
            players,
            handContracts,
            handRotator,
            handCardMultimap,
            centerCardHandMap
        );
    }

    public Deal buildDeal(String name) {
        Deal deal = new Deal();
        deal.setCreated(Clock.getNow());
        deal.setFirstTurn(firstTurn);
        deal.setName(name);
        deal.setPlayers(players);
        deal.setWidow(widow);
        initContracts(deal);
        initHandCards(deal);
        initCenterCards(deal);
        return deal;
    }

    private void initCenterCards(Deal dto) {
        Map<Card, Hand> centerCards = getCenterCards();
        for (Map.Entry<Card, Hand> cardHandEntry : centerCards.entrySet()) {
            switch (cardHandEntry.getValue()) {
                case EAST:
                    dto.setCenterEastCard(cardHandEntry.getKey());
                    break;
                case SOUTH:
                    dto.setCenterSouthCard(cardHandEntry.getKey());
                    break;
                case WEST:
                    dto.setCenterWestCard(cardHandEntry.getKey());
                    break;
                default:
                    throw new IllegalStateException("Invalid Hand constant: " + cardHandEntry.getValue());
            }
        }
    }

    private void initHandCards(Deal dto) {
        Map<TableLocation, Collection<Card>> tableCards = getTableCards();
        dto.setEastCards(getHandCards(tableCards, TableLocation.EAST));
        dto.setSouthCards(getHandCards(tableCards, TableLocation.SOUTH));
        dto.setWestCards(getHandCards(tableCards, TableLocation.WEST));
    }

    private void initContracts(Deal dto) {
        Map<Hand, Contract> handContracts = getHandContracts();
        dto.setEastContract(handContracts.get(Hand.EAST));
        dto.setSouthContract(handContracts.get(Hand.SOUTH));
        dto.setWestContract(handContracts.get(Hand.WEST));
    }

    private List<Card> getHandCards(Map<TableLocation, Collection<Card>> tableCards, TableLocation location) {
        Collection<Card> cardsCollection = tableCards.get(location);
        List<Card> cards = newArrayList();
        if (cardsCollection == null) {
            cards = Collections.emptyList();
        } else {
            cards.addAll(cardsCollection);
        }
        return cards;
    }

    public Hand getFirstTurn() {
        return firstTurn;
    }

    public Map<Hand, Contract> getHandContracts() {
        return handContracts;
    }

    public Map<TableLocation, Collection<Card>> getTableCards() {
        return GameUtils.copyDefensive(handCardMultimap);
    }

    public Map<Card, Hand> getCenterCards() {
        return new LinkedHashMap<Card, Hand>(centerCardHandMap);
    }


}
