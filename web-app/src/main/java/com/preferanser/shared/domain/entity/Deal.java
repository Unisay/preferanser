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

package com.preferanser.shared.domain.entity;


import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Index;
import com.preferanser.shared.domain.*;
import com.preferanser.shared.dto.Dto;

import javax.validation.constraints.Size;
import java.util.*;

import static com.google.common.collect.Lists.newArrayList;

@Entity
@SuppressWarnings({"unused", "ClassWithTooManyFields", "ClassWithTooManyMethods"})
public class Deal extends BaseEntity implements Dto {

    @Index
    @Size(min = 2, max = 32)
    private String name;

    @Index
    private String userId;
    private Date created;
    private Cardinal firstTurn;
    private Contract northContract;
    private Contract eastContract;
    private Contract southContract;
    private Contract westContract;
    private List<Card> northCards;
    private List<Card> eastCards;
    private List<Card> southCards;
    private List<Card> westCards;
    private Card centerNorthCard;
    private Card centerEastCard;
    private Card centerSouthCard;
    private Card centerWestCard;

    public Deal() {
        northCards = new ArrayList<Card>();
        eastCards = new ArrayList<Card>();
        southCards = new ArrayList<Card>();
        westCards = new ArrayList<Card>();
    }

    public static Deal fromGameBuilder(GameBuilder gameBuilder) {
        Deal dto = new Deal();
        dto.created = new Date();
        dto.firstTurn = gameBuilder.getFirstTurn();
        dto.name = new Date().toString(); // TODO: ask user for a name
        initContracts(gameBuilder, dto);
        initCardinalCards(gameBuilder, dto);
        initCenterCards(gameBuilder, dto);
        return dto;
    }

    private static void initCenterCards(GameBuilder gameBuilder, Deal dto) {
        Map<Card, Cardinal> centerCards = gameBuilder.getCenterCards();
        for (Map.Entry<Card, Cardinal> cardCardinalEntry : centerCards.entrySet()) {
            switch (cardCardinalEntry.getValue()) {
                case NORTH:
                    dto.centerNorthCard = cardCardinalEntry.getKey();
                    break;
                case EAST:
                    dto.centerEastCard = cardCardinalEntry.getKey();
                    break;
                case SOUTH:
                    dto.centerSouthCard = cardCardinalEntry.getKey();
                    break;
                case WEST:
                    dto.centerWestCard = cardCardinalEntry.getKey();
                    break;
                default:
                    throw new IllegalStateException("Invalid Cardinal constant: " + cardCardinalEntry.getValue());
            }
        }
    }

    private static void initCardinalCards(GameBuilder gameBuilder, Deal dto) {
        Map<TableLocation, Collection<Card>> tableCards = gameBuilder.getTableCards();
        dto.northCards.addAll(getCardinalCards(tableCards, TableLocation.NORTH));
        dto.eastCards.addAll(getCardinalCards(tableCards, TableLocation.EAST));
        dto.southCards.addAll(getCardinalCards(tableCards, TableLocation.SOUTH));
        dto.westCards.addAll(getCardinalCards(tableCards, TableLocation.WEST));
    }

    private static void initContracts(GameBuilder gameBuilder, Deal dto) {
        Map<Cardinal, Contract> cardinalContracts = gameBuilder.getCardinalContracts();
        dto.northContract = cardinalContracts.get(Cardinal.NORTH);
        dto.eastContract = cardinalContracts.get(Cardinal.EAST);
        dto.southContract = cardinalContracts.get(Cardinal.SOUTH);
        dto.westContract = cardinalContracts.get(Cardinal.WEST);
    }

    private static List<Card> getCardinalCards(Map<TableLocation, Collection<Card>> tableCards, TableLocation location) {
        Collection<Card> cardsCollection = tableCards.get(location);
        List<Card> cards = newArrayList();
        if (cardsCollection == null) {
            cards = Collections.emptyList();
        } else {
            cards.addAll(cardsCollection);
        }
        return cards;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Cardinal getFirstTurn() {
        return firstTurn;
    }

    public void setFirstTurn(Cardinal firstTurn) {
        this.firstTurn = firstTurn;
    }

    public Contract getNorthContract() {
        return northContract;
    }

    public void setNorthContract(Contract northContract) {
        this.northContract = northContract;
    }

    public Contract getEastContract() {
        return eastContract;
    }

    public void setEastContract(Contract eastContract) {
        this.eastContract = eastContract;
    }

    public Contract getSouthContract() {
        return southContract;
    }

    public void setSouthContract(Contract southContract) {
        this.southContract = southContract;
    }

    public Contract getWestContract() {
        return westContract;
    }

    public void setWestContract(Contract westContract) {
        this.westContract = westContract;
    }

    public List<Card> getNorthCards() {
        return northCards;
    }

    public void setNorthCards(List<Card> northCards) {
        this.northCards = northCards;
    }

    public List<Card> getEastCards() {
        return eastCards;
    }

    public void setEastCards(List<Card> eastCards) {
        this.eastCards = eastCards;
    }

    public List<Card> getSouthCards() {
        return southCards;
    }

    public void setSouthCards(List<Card> southCards) {
        this.southCards = southCards;
    }

    public List<Card> getWestCards() {
        return westCards;
    }

    public void setWestCards(List<Card> westCards) {
        this.westCards = westCards;
    }

    public Card getCenterNorthCard() {
        return centerNorthCard;
    }

    public void setCenterNorthCard(Card centerNorthCard) {
        this.centerNorthCard = centerNorthCard;
    }

    public Card getCenterEastCard() {
        return centerEastCard;
    }

    public void setCenterEastCard(Card centerEastCard) {
        this.centerEastCard = centerEastCard;
    }

    public Card getCenterSouthCard() {
        return centerSouthCard;
    }

    public void setCenterSouthCard(Card centerSouthCard) {
        this.centerSouthCard = centerSouthCard;
    }

    public Card getCenterWestCard() {
        return centerWestCard;
    }

    public void setCenterWestCard(Card centerWestCard) {
        this.centerWestCard = centerWestCard;
    }
}
