package com.preferanser.shared.domain;

import com.google.common.base.Objects;

import java.util.Date;
import java.util.List;

public class Drawing {

    private Long id;
    private long userId;
    private long dealId;
    private String name;
    private String description;
    private List<Card> turns;
    private Date createdAt;

    @com.fasterxml.jackson.annotation.JsonCreator
    @org.codehaus.jackson.annotate.JsonCreator
    public Drawing(
        @org.codehaus.jackson.annotate.JsonProperty("id") @com.fasterxml.jackson.annotation.JsonProperty("id") Long id,
        @org.codehaus.jackson.annotate.JsonProperty("userId") @com.fasterxml.jackson.annotation.JsonProperty("userId") long userId,
        @org.codehaus.jackson.annotate.JsonProperty("dealId") @com.fasterxml.jackson.annotation.JsonProperty("dealId") long dealId,
        @org.codehaus.jackson.annotate.JsonProperty("name") @com.fasterxml.jackson.annotation.JsonProperty("name") String name,
        @org.codehaus.jackson.annotate.JsonProperty("description") @com.fasterxml.jackson.annotation.JsonProperty("description") String description,
        @org.codehaus.jackson.annotate.JsonProperty("turns") @com.fasterxml.jackson.annotation.JsonProperty("turns") List<Card> turns,
        @org.codehaus.jackson.annotate.JsonProperty("createdAt") @com.fasterxml.jackson.annotation.JsonProperty("createdAt") Date createdAt
    ) {
        this.id = id;
        this.userId = userId;
        this.dealId = dealId;
        this.name = name;
        this.description = description;
        this.turns = turns;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public long getUserId() {
        return userId;
    }

    public long getDealId() {
        return dealId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<Card> getTurns() {
        return turns;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Drawing that = (Drawing) o;

        return Objects.equal(this.id, that.id) &&
            Objects.equal(this.userId, that.userId) &&
            Objects.equal(this.dealId, that.dealId) &&
            Objects.equal(this.name, that.name) &&
            Objects.equal(this.description, that.description) &&
            Objects.equal(this.turns, that.turns) &&
            Objects.equal(this.createdAt, that.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, userId, dealId, name, description, turns, createdAt);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
            .add("id", id)
            .add("userId", userId)
            .add("dealId", dealId)
            .add("name", name)
            .add("description", description)
            .add("turns", turns)
            .add("createdAt", createdAt)
            .toString();
    }
}
