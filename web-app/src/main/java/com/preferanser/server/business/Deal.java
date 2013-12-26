package com.preferanser.server.business;

import com.googlecode.objectify.annotation.Entity;
import com.preferanser.domain.Card;
import com.preferanser.domain.Cardinal;
import com.preferanser.domain.Contract;

import java.util.Date;
import java.util.List;

@Entity
public class Deal extends DatastoreObject {

    @SuppressWarnings("unused")
    public Deal() {
        // Default no-arg constructor required by RequestFactory
    }

    private String userId;
    private String name;
    private Date created;

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

    private Cardinal firstTurn;

    public Deal(String name,
                Date created,
                Contract northContract,
                Contract eastContract,
                Contract southContract,
                Contract westContract,
                List<Card> northCards,
                List<Card> eastCards,
                List<Card> southCards,
                List<Card> westCards,
                Card centerNorthCard,
                Card centerEastCard,
                Card centerSouthCard,
                Card centerWestCard,
                Cardinal firstTurn
    ) {
        this.name = name;
        this.created = created;
        this.northContract = northContract;
        this.eastContract = eastContract;
        this.southContract = southContract;
        this.westContract = westContract;
        this.northCards = northCards;
        this.eastCards = eastCards;
        this.southCards = southCards;
        this.westCards = westCards;
        this.centerNorthCard = centerNorthCard;
        this.centerEastCard = centerEastCard;
        this.centerSouthCard = centerSouthCard;
        this.centerWestCard = centerWestCard;
        this.firstTurn = firstTurn;
    }

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public Date getCreated() {
        return created;
    }

    public Contract getNorthContract() {
        return northContract;
    }

    public Contract getEastContract() {
        return eastContract;
    }

    public Contract getSouthContract() {
        return southContract;
    }

    public Contract getWestContract() {
        return westContract;
    }

    public List<Card> getNorthCards() {
        return northCards;
    }

    public List<Card> getEastCards() {
        return eastCards;
    }

    public List<Card> getSouthCards() {
        return southCards;
    }

    public List<Card> getWestCards() {
        return westCards;
    }

    public Card getCenterNorthCard() {
        return centerNorthCard;
    }

    public Card getCenterEastCard() {
        return centerEastCard;
    }

    public Card getCenterSouthCard() {
        return centerSouthCard;
    }

    public Card getCenterWestCard() {
        return centerWestCard;
    }

    public Cardinal getFirstTurn() {
        return firstTurn;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public void setNorthContract(Contract northContract) {
        this.northContract = northContract;
    }

    public void setEastContract(Contract eastContract) {
        this.eastContract = eastContract;
    }

    public void setSouthContract(Contract southContract) {
        this.southContract = southContract;
    }

    public void setWestContract(Contract westContract) {
        this.westContract = westContract;
    }

    public void setNorthCards(List<Card> northCards) {
        this.northCards = northCards;
    }

    public void setEastCards(List<Card> eastCards) {
        this.eastCards = eastCards;
    }

    public void setSouthCards(List<Card> southCards) {
        this.southCards = southCards;
    }

    public void setWestCards(List<Card> westCards) {
        this.westCards = westCards;
    }

    public void setCenterNorthCard(Card centerNorthCard) {
        this.centerNorthCard = centerNorthCard;
    }

    public void setCenterEastCard(Card centerEastCard) {
        this.centerEastCard = centerEastCard;
    }

    public void setCenterSouthCard(Card centerSouthCard) {
        this.centerSouthCard = centerSouthCard;
    }

    public void setCenterWestCard(Card centerWestCard) {
        this.centerWestCard = centerWestCard;
    }

    public void setFirstTurn(Cardinal firstTurn) {
        this.firstTurn = firstTurn;
    }
}
