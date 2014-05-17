package com.preferanser.server.entity;

import com.google.common.base.Objects;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Parent;
import com.preferanser.shared.domain.Card;

import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;

@com.googlecode.objectify.annotation.Entity
@SuppressWarnings({"unused", "ClassWithTooManyFields", "ClassWithTooManyMethods"})
public class DrawingEntity implements Entity {

    @Id
    private Long id;

    @Parent
    private Key<DealEntity> deal;

    @Index
    @Size(min = 1, max = 32)
    private String name;

    @Size(max = 512)
    private String description;

    @Index
    private Date created;

    private List<Card> turns;

    public Key<DrawingEntity> getKey() {
        return Key.create(this);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Key<DealEntity> getDeal() {
        return deal;
    }

    public void setDeal(long userId, long dealId) {
        Key<UserEntity> userKey = Key.create(UserEntity.class, userId);
        this.deal = Key.create(userKey, DealEntity.class, dealId);
    }

    public void setDeal(DealEntity dealEntity) {
        this.deal = Key.create(dealEntity.getOwner(), DealEntity.class, dealEntity.getId());
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

    public List<Card> getTurns() {
        return turns;
    }

    public void setTurns(List<Card> turns) {
        this.turns = turns;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DrawingEntity that = (DrawingEntity) o;

        return Objects.equal(this.id, that.id) &&
            Objects.equal(this.deal, that.deal) &&
            Objects.equal(this.name, that.name) &&
            Objects.equal(this.description, that.description) &&
            Objects.equal(this.created, that.created) &&
            Objects.equal(this.turns, that.turns);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, deal, name, description, created, turns);
    }

    @Override public String toString() {
        return Objects.toStringHelper(this)
            .add("id", id)
            .add("deal", deal)
            .add("name", name)
            .add("description", description)
            .add("created", created)
            .add("turns", turns)
            .toString();
    }

}
