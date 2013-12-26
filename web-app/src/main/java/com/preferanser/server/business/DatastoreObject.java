package com.preferanser.server.business;

import com.googlecode.objectify.annotation.Id;

import javax.persistence.PrePersist;

public class DatastoreObject {

    @Id
    @javax.persistence.Id
    private Long id;

    private Integer version = 0;

    @PrePersist void onPersist() {
        this.version++;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
