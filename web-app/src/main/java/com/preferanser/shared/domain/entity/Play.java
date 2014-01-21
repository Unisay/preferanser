package com.preferanser.shared.domain.entity;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Index;
import com.preferanser.shared.domain.*;
import com.preferanser.shared.dto.Dto;

import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Entity
@SuppressWarnings({"unused", "ClassWithTooManyFields", "ClassWithTooManyMethods"})
public class Play extends BaseEntity implements Dto {

    @Index
    @Size(min = 2, max = 32)
    private String name;

    @Size(max = 512)
    private String description;

    @Index
    private String userId;

    @Index
    private Date created;
    private Hand firstTurn;
    private Players players;
    private Contract eastContract;
    private Contract southContract;
    private Contract westContract;
    private Widow widow;
    private Set<Card> eastCards;
    private Set<Card> southCards;
    private Set<Card> westCards;
    private List<Turn> turns;
    private int currentTrickIndex;

    public Play() {
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Hand getFirstTurn() {
        return firstTurn;
    }

    public void setFirstTurn(Hand firstTurn) {
        this.firstTurn = firstTurn;
    }

    public Players getPlayers() {
        return players;
    }

    public void setPlayers(Players players) {
        this.players = players;
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

    public Set<Card> getEastCards() {
        return eastCards;
    }

    public void setEastCards(Set<Card> eastCards) {
        this.eastCards = eastCards;
    }

    public Set<Card> getSouthCards() {
        return southCards;
    }

    public void setSouthCards(Set<Card> southCards) {
        this.southCards = southCards;
    }

    public Set<Card> getWestCards() {
        return westCards;
    }

    public void setWestCards(Set<Card> westCards) {
        this.westCards = westCards;
    }

    public List<Turn> getTurns() {
        return turns;
    }

    public void setTurns(List<Turn> turns) {
        this.turns = turns;
    }

    public int getCurrentTrickIndex() {
        return currentTrickIndex;
    }

    public void setCurrentTrickIndex(int currentTrickIndex) {
        this.currentTrickIndex = currentTrickIndex;
    }

    public Map<Hand, Contract> getHandContracts() {
        ImmutableMap.Builder<Hand, Contract> builder = ImmutableMap.builder();
        if (eastContract != null)
            builder.put(Hand.EAST, eastContract);
        if (southContract != null)
            builder.put(Hand.SOUTH, southContract);
        if (westContract != null)
            builder.put(Hand.WEST, westContract);
        return builder.build();
    }

    public Multimap<Hand, Card> getHandCards() {
        ImmutableSetMultimap.Builder<Hand, Card> builder = ImmutableSetMultimap.builder();
        if (eastCards != null)
            builder.putAll(Hand.EAST, eastCards);
        if (southCards != null)
            builder.putAll(Hand.SOUTH, southCards);
        if (westCards != null)
            builder.putAll(Hand.WEST, westCards);
        return builder.build();
    }

}