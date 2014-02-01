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
import com.google.common.base.Strings;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Maps;
import com.preferanser.shared.domain.entity.Deal;
import com.preferanser.shared.domain.exception.DuplicateGameTurnException;
import com.preferanser.shared.domain.exception.EditorException;
import com.preferanser.shared.domain.exception.validation.*;
import com.preferanser.shared.util.Clock;
import com.preferanser.shared.util.EnumRotator;
import com.preferanser.shared.util.GameUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;

public class Editor {

    private static final int NUM_OF_CARDS_PER_HAND = 10;
    private static final int NUM_OF_CONTRACTS = 3;

    private Long id;
    private String name;
    private String description;
    private Widow widow;
    private Players players;
    private Hand firstTurn;
    private Map<Card, Hand> centerCardHandMap;
    private Map<Hand, Contract> handContracts;
    private LinkedHashMultimap<Hand, Card> handCardMultimap;

    public Editor() {
        reset();
    }

    public Editor reset() {
        name = null;
        description = null;
        firstTurn = null;
        players = null;
        widow = new Widow();
        handContracts = Maps.newHashMapWithExpectedSize(Hand.PLAYING_HANDS.size());
        centerCardHandMap = Maps.newLinkedHashMap(); // order is important
        handCardMultimap = LinkedHashMultimap.create(TableLocation.values().length, Card.values().length);
        return this;
    }

    public Editor setFirstTurn(Hand firstTurn) {
        this.firstTurn = firstTurn;
        return this;
    }

    public Editor setThreePlayers() {
        players = Players.THREE;
        return this;
    }

    public Editor setFourPlayers() {
        players = Players.FOUR;
        return this;
    }

    public String getName() {
        return name;
    }

    public Editor setName(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Editor setDescription(String description) {
        this.description = description;
        return this;
    }

    public Editor setWidow(Widow widow) {
        this.widow = widow;
        return this;
    }

    public Widow getWidow() {
        return widow;
    }

    public Editor setHandContract(Hand hand, Contract contract) {
        Preconditions.checkNotNull(hand);
        Preconditions.checkNotNull(contract);
        handContracts.put(hand, contract);
        return this;
    }

    public Editor setDeal(Deal deal) {
        id = deal.getId();
        name = deal.getName();
        description = deal.getDescription();
        firstTurn = deal.getFirstTurn();
        widow = deal.getWidow();
        handContracts.clear();
        if (deal.getEastContract() != null)
            handContracts.put(Hand.EAST, deal.getEastContract());
        if (deal.getSouthContract() != null)
            handContracts.put(Hand.SOUTH, deal.getSouthContract());
        if (deal.getWestContract() != null)
            handContracts.put(Hand.WEST, deal.getWestContract());
        handCardMultimap.clear();
        handCardMultimap.putAll(Hand.EAST, deal.getEastCards());
        handCardMultimap.putAll(Hand.SOUTH, deal.getSouthCards());
        handCardMultimap.putAll(Hand.WEST, deal.getWestCards());
        return this;
    }

    public Editor putCards(Hand hand, Collection<Card> cards) {
        handCardMultimap.putAll(hand, cards);
        return this;
    }

    public Editor putCards(Hand hand, Card... cards) {
        putCards(hand, newArrayList(cards));
        return this;
    }

    public Editor clearCards(TableLocation... tableLocations) {
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

    public boolean moveCard(Card card, TableLocation newLocation) throws DuplicateGameTurnException {
        TableLocation oldLocation = findCardTableLocation(card);
        return removeCardFromOldLocation(card, oldLocation)
            && addCardToNewLocation(card, oldLocation, newLocation);
    }

    private TableLocation findCardTableLocation(Card card) {
        for (Map.Entry<Hand, Card> entry : handCardMultimap.entries()) {
            if (entry.getValue() == card)
                return TableLocation.valueOf(entry.getKey());
        }
        if (widow.containsCard(card)) // TODO unit-test
            return TableLocation.WIDOW;

        checkArgument(centerCardHandMap.containsKey(card), "Cant find card %s on the table", card);
        return TableLocation.CENTER;
    }

    private boolean removeCardFromOldLocation(Card card, TableLocation oldLocation) {
        switch (oldLocation) {
            case CENTER:
                checkNotNull(centerCardHandMap.remove(card), "There is no %s in TableLocation.CENTER", card);
                return true;
            case WIDOW: // TODO unit-test
                widow.remove(card);
                handCardMultimap.replaceValues(Hand.WIDOW, widow.asSet());
                return true;
            default:
                Hand oldHand = Hand.valueOf(oldLocation);
                checkArgument(handCardMultimap.get(oldHand).contains(card), "There is no %s in Hand.%s", card, oldHand);
                return handCardMultimap.remove(oldHand, card);
        }
    }

    private boolean addCardToNewLocation(Card card, TableLocation oldLocation, TableLocation newLocation) throws DuplicateGameTurnException {
        switch (newLocation) {
            case CENTER:
                if (centerCardHandMap.size() == players.getNumPlayers())
                    return false;
                Hand oldHand = Hand.valueOf(oldLocation);
                if (centerCardHandMap.containsValue(oldHand)) // TODO unit-test
                    throw new DuplicateGameTurnException(centerCardHandMap, oldHand);
                centerCardHandMap.put(card, oldHand);
                break;
            case WIDOW:
                return widow.add(card); // TODO unit-test
            default:
                handCardMultimap.put(Hand.valueOf(newLocation), card);
        }
        return true;
    }

    private Optional<List<EditorValidationError>> validate() {
        List<EditorValidationError> errors = newArrayList();

        if (Strings.isNullOrEmpty(name))
            errors.add(new DealNameNotSpecifiedValidationError()); // TODO: also check invalid names

        if (players == null)
            errors.add(new NumPlayersNotSpecifiedValidationError());

        if (!widow.hasTwoCards())
            errors.add(new WidowNotSpecifiedValidationError()); // TODO: unit test this check

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
        Set<Card> cardSet = newHashSet();
        Set<Card> duplicateCardSet = newHashSet();
        for (Card card : handCardMultimap.values()) {
            if (!cardSet.add(card)) {
                duplicateCardSet.add(card);
            }
        }
        return duplicateCardSet;
    }

    private Map<Hand, Integer> wrongNumberOfCardsPerHand() {
        Map<Hand, Integer> wrongHands = newHashMap();
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

    public Deal build() throws EditorException {
        Optional<List<EditorValidationError>> validationErrors = validate();
        if (validationErrors.isPresent())
            throw new EditorException(validationErrors.get());

        EnumRotator<Hand> handRotator = new EnumRotator<Hand>(Hand.values(), firstTurn);
        handRotator.setSkipValues(Hand.WIDOW);

        Deal deal = new Deal();
        deal.setId(id);
        deal.setCreated(Clock.getNow());
        deal.setName(name);
        deal.setDescription(description);
        deal.setPlayers(players);
        deal.setFirstTurn(firstTurn);
        deal.setShared(false); // consider setting it in the interface
        deal.setWidow(widow);
        deal.setEastContract(handContracts.get(Hand.EAST));
        deal.setSouthContract(handContracts.get(Hand.SOUTH));
        deal.setWestContract(handContracts.get(Hand.WEST));
        deal.setEastCards(handCardMultimap.get(Hand.EAST));
        deal.setSouthCards(handCardMultimap.get(Hand.SOUTH));
        deal.setWestCards(handCardMultimap.get(Hand.WEST));
        deal.setCurrentTrickIndex(0);
        return deal;
    }

    public Hand getFirstTurn() {
        return firstTurn;
    }

    public Map<Hand, Contract> getHandContracts() {
        return handContracts;
    }

    public Map<Hand, Set<Card>> getHandCards() {
        return GameUtils.copyDefensive(handCardMultimap);
    }

    public Map<Card, Hand> getCenterCards() {
        return Maps.newLinkedHashMap(centerCardHandMap);
    }
}
