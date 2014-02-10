package com.preferanser.shared.domain;

import com.google.common.base.Objects;

import java.io.Serializable;
import java.util.Date;

public class DealInfo implements Serializable {

    private Long id;
    private String name;
    private String description;
    private String ownerId;
    private Date created;

    public DealInfo() {
    }

    public DealInfo(Long id, String name, String description, String ownerId, Date created) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.ownerId = ownerId;
        this.created = created;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public Date getCreated() {
        return created;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DealInfo that = (DealInfo) o;

        return Objects.equal(this.id, that.id) &&
            Objects.equal(this.name, that.name) &&
            Objects.equal(this.description, that.description) &&
            Objects.equal(this.ownerId, that.ownerId) &&
            Objects.equal(this.created, that.created);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, name, description, ownerId, created);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
            .addValue(id)
            .addValue(name)
            .addValue(description)
            .addValue(ownerId)
            .addValue(created)
            .toString();
    }
}