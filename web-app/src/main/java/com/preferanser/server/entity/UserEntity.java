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

package com.preferanser.server.entity;


import com.google.common.base.Objects;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Entity
public class UserEntity implements com.preferanser.server.entity.Entity {

    @Id
    private Long id;

    @Index
    private String googleId;
    private String email;
    private boolean admin;

    public UserEntity() {
    }

    public UserEntity(Long id, String googleId, String email, boolean admin) {
        this.id = id;
        this.googleId = googleId;
        this.email = email;
        this.admin = admin;
    }

    public Key<UserEntity> getKey() {
        return Key.create(this);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

    public boolean getAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserEntity that = (UserEntity) o;

        return Objects.equal(this.id, that.id) &&
            Objects.equal(this.googleId, that.googleId) &&
            Objects.equal(this.email, that.email) &&
            Objects.equal(this.admin, that.admin);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, googleId, email, admin);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
            .addValue(id)
            .addValue(googleId)
            .addValue(email)
            .addValue(admin)
            .toString();
    }
}