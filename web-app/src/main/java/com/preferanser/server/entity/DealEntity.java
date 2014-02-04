package com.preferanser.server.entity;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Parent;
import com.preferanser.shared.domain.*;

import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;

@com.googlecode.objectify.annotation.Entity
@SuppressWarnings({"unused", "ClassWithTooManyFields", "ClassWithTooManyMethods"})
public class DealEntity implements Entity {

    @Id
    private Long id;

    @Parent
    private Key<UserEntity> owner;

    @Index
    @Size(min = 2, max = 32)
    private String name;

    @Size(max = 512)
    private String description;

    @Index
    private Date created;

    @Index
    private boolean shared;

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

    public DealEntity() {
        widow = new Widow();
        eastCards = newHashSet();
        westCards = newHashSet();
        southCards = newHashSet();
        turns = newArrayList();
    }

    public DealEntity(DealEntity deal) {
        id = deal.id;
        name = deal.name;
        description = deal.description;
        owner = deal.owner;
        created = deal.created == null ? null : new Date(deal.created.getTime());
        shared = deal.shared;
        firstTurn = deal.firstTurn;
        players = deal.players;
        eastContract = deal.eastContract;
        southContract = deal.southContract;
        westContract = deal.westContract;
        widow = deal.widow == null ? new Widow() : new Widow(deal.widow);
        eastCards = deal.eastCards == null ? Sets.<Card>newHashSet() : newHashSet(deal.eastCards);
        southCards = deal.southCards == null ? Sets.<Card>newHashSet() : newHashSet(deal.southCards);
        westCards = deal.westCards == null ? Sets.<Card>newHashSet() : newHashSet(deal.westCards);
        turns = deal.turns == null ? Lists.<Turn>newArrayList() : newArrayList(deal.turns);
        currentTrickIndex = deal.currentTrickIndex;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Key<UserEntity> getOwner() {
        return owner;
    }

    public void setOwner(UserEntity user) {
        owner = Key.create(UserEntity.class, user.getId());
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public boolean isShared() {
        return shared;
    }

    public void setShared(boolean shared) {
        this.shared = shared;
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
        this.eastCards = eastCards == null ? Sets.<Card>newHashSet() : eastCards;
    }

    public Set<Card> getSouthCards() {
        return southCards;
    }

    public void setSouthCards(Set<Card> southCards) {
        this.southCards = southCards == null ? Sets.<Card>newHashSet() : southCards;
    }

    public Set<Card> getWestCards() {
        return westCards;
    }

    public void setWestCards(Set<Card> westCards) {
        this.westCards = westCards == null ? Sets.<Card>newHashSet() : westCards;
    }

    public List<Turn> getTurns() {
        return turns;
    }

    public void setTurns(List<Turn> turns) {
        this.turns = turns == null ? Lists.<Turn>newArrayList() : turns;
    }

    public int getCurrentTrickIndex() {
        return currentTrickIndex;
    }

    public void setCurrentTrickIndex(int currentTrickIndex) {
        this.currentTrickIndex = currentTrickIndex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DealEntity that = (DealEntity) o;

        return Objects.equal(this.name, that.name) &&
            Objects.equal(this.description, that.description) &&
            Objects.equal(this.owner, that.owner) &&
            Objects.equal(this.created, that.created) &&
            Objects.equal(this.shared, that.shared) &&
            Objects.equal(this.firstTurn, that.firstTurn) &&
            Objects.equal(this.players, that.players) &&
            Objects.equal(this.eastContract, that.eastContract) &&
            Objects.equal(this.southContract, that.southContract) &&
            Objects.equal(this.westContract, that.westContract) &&
            Objects.equal(this.widow, that.widow) &&
            Objects.equal(this.eastCards, that.eastCards) &&
            Objects.equal(this.southCards, that.southCards) &&
            Objects.equal(this.westCards, that.westCards) &&
            Objects.equal(this.turns, that.turns) &&
            Objects.equal(this.currentTrickIndex, that.currentTrickIndex) &&
            Objects.equal(this.id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name, description, owner, created, shared, firstTurn,
            players, eastContract, southContract, westContract, widow,
            eastCards, southCards, westCards, turns, currentTrickIndex,
            id);
    }

    @Override public String toString() {
        final StringBuilder sb = new StringBuilder("DealEntity{");
        sb.append("name='").append(name).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append(", id='").append(id).append('\'');
        sb.append(", owner='").append(owner).append('\'');
        sb.append(", created=").append(created);
        sb.append(", shared=").append(shared);
        sb.append(", firstTurn=").append(firstTurn);
        sb.append(", players=").append(players);
        sb.append(", eastContract=").append(eastContract);
        sb.append(", southContract=").append(southContract);
        sb.append(", westContract=").append(westContract);
        sb.append(", widow=").append(widow);
        sb.append(", eastCards=").append(eastCards);
        sb.append(", southCards=").append(southCards);
        sb.append(", westCards=").append(westCards);
        sb.append(", turns=").append(turns);
        sb.append(", currentTrickIndex=").append(currentTrickIndex);
        sb.append('}');
        return sb.toString();
    }

}
