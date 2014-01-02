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


import com.google.common.base.Objects;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Index;
import com.preferanser.shared.domain.*;
import com.preferanser.shared.dto.Dto;

import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@SuppressWarnings({"unused", "ClassWithTooManyFields", "ClassWithTooManyMethods"})
public class Deal extends BaseEntity implements Dto {

    @Index
    @Size(min = 2, max = 32)
    private String name;

    @Size(max = 512)
    private String description;

    @Index
    private String userId;

    @Index
    private Date created;

    private Cardinal firstTurn;

    private GamePlayers gamePlayers;

    private Contract northContract;
    private Contract eastContract;
    private Contract southContract;
    private Contract westContract;
    private Widow widow;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public GamePlayers getGamePlayers() {
        return gamePlayers;
    }

    public void setGamePlayers(GamePlayers gamePlayers) {
        this.gamePlayers = gamePlayers;
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

    public Widow getWidow() {
        return widow;
    }

    public void setWidow(Widow widow) {
        this.widow = widow;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Deal deal = (Deal) o;

        if (centerEastCard != deal.centerEastCard) return false;
        if (centerNorthCard != deal.centerNorthCard) return false;
        if (centerSouthCard != deal.centerSouthCard) return false;
        if (centerWestCard != deal.centerWestCard) return false;
        if (created != null ? !created.equals(deal.created) : deal.created != null) return false;
        if (description != null ? !description.equals(deal.description) : deal.description != null) return false;
        if (eastCards != null ? !eastCards.equals(deal.eastCards) : deal.eastCards != null) return false;
        if (eastContract != deal.eastContract) return false;
        if (firstTurn != deal.firstTurn) return false;
        if (gamePlayers != deal.gamePlayers) return false;
        if (name != null ? !name.equals(deal.name) : deal.name != null) return false;
        if (northCards != null ? !northCards.equals(deal.northCards) : deal.northCards != null) return false;
        if (northContract != deal.northContract) return false;
        if (southCards != null ? !southCards.equals(deal.southCards) : deal.southCards != null) return false;
        if (southContract != deal.southContract) return false;
        if (userId != null ? !userId.equals(deal.userId) : deal.userId != null) return false;
        if (westCards != null ? !westCards.equals(deal.westCards) : deal.westCards != null) return false;
        if (westContract != deal.westContract) return false;
        //if (widow != null ? !widow.equals(deal.widow) : deal.widow != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        result = 31 * result + (created != null ? created.hashCode() : 0);
        result = 31 * result + (firstTurn != null ? firstTurn.hashCode() : 0);
        result = 31 * result + (gamePlayers != null ? gamePlayers.hashCode() : 0);
        result = 31 * result + (northContract != null ? northContract.hashCode() : 0);
        result = 31 * result + (eastContract != null ? eastContract.hashCode() : 0);
        result = 31 * result + (southContract != null ? southContract.hashCode() : 0);
        result = 31 * result + (westContract != null ? westContract.hashCode() : 0);
        result = 31 * result + (widow != null ? widow.hashCode() : 0);
        result = 31 * result + (northCards != null ? northCards.hashCode() : 0);
        result = 31 * result + (eastCards != null ? eastCards.hashCode() : 0);
        result = 31 * result + (southCards != null ? southCards.hashCode() : 0);
        result = 31 * result + (westCards != null ? westCards.hashCode() : 0);
        result = 31 * result + (centerNorthCard != null ? centerNorthCard.hashCode() : 0);
        result = 31 * result + (centerEastCard != null ? centerEastCard.hashCode() : 0);
        result = 31 * result + (centerSouthCard != null ? centerSouthCard.hashCode() : 0);
        result = 31 * result + (centerWestCard != null ? centerWestCard.hashCode() : 0);
        return result;
    }

    @Override public String toString() {
        return Objects.toStringHelper(this)
            .add("name", name)
            .add("description", description)
            .add("userId", userId)
            .add("created", created)
            .add("firstTurn", firstTurn)
            .add("gamePlayers", gamePlayers)
            .add("northContract", northContract)
            .add("eastContract", eastContract)
            .add("southContract", southContract)
            .add("westContract", westContract)
            .add("widow", widow)
            .add("northCards", northCards)
            .add("eastCards", eastCards)
            .add("southCards", southCards)
            .add("westCards", westCards)
            .add("centerNorthCard", centerNorthCard)
            .add("centerEastCard", centerEastCard)
            .add("centerSouthCard", centerSouthCard)
            .add("centerWestCard", centerWestCard)
            .toString();
    }
}
