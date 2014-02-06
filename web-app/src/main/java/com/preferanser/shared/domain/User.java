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

package com.preferanser.shared.domain;

import com.google.common.base.Objects;

@SuppressWarnings("unused")
public class User {

    private Boolean admin;
    private Boolean loggedIn;
    private String logoutUrl;
    private String loginUrl;
    private String email;
    private String nickname;

    public User() {
        this.admin = false;
        this.loggedIn = false;
    }

    public User(Boolean admin, Boolean loggedIn, String logoutUrl, String loginUrl, String email, String nickname) {
        this.admin = admin;
        this.loggedIn = loggedIn;
        this.logoutUrl = logoutUrl;
        this.loginUrl = loginUrl;
        this.email = email;
        this.nickname = nickname;
    }

    public User copyFrom(User currentUser) {
        admin = currentUser.admin;
        email = currentUser.email;
        loggedIn = currentUser.loggedIn;
        logoutUrl = currentUser.logoutUrl;
        loginUrl = currentUser.loginUrl;
        nickname = currentUser.nickname;
        return this;
    }

    public Boolean getAdmin() {
        return admin;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }

    public Boolean getLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(Boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public String getLogoutUrl() {
        return logoutUrl;
    }

    public void setLogoutUrl(String logoutUrl) {
        this.logoutUrl = logoutUrl;
    }

    public String getLoginUrl() {
        return loginUrl;
    }

    public void setLoginUrl(String loginUrl) {
        this.loginUrl = loginUrl;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    @Override public String toString() {
        return Objects.toStringHelper(this)
            .add("getAdmin", admin)
            .add("email", email)
            .add("loggedIn", loggedIn)
            .add("logoutUrl", logoutUrl)
            .add("loginUrl", loginUrl)
            .add("nickname", nickname)
            .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User that = (User) o;

        return Objects.equal(this.admin, that.admin) &&
            Objects.equal(this.email, that.email) &&
            Objects.equal(this.loggedIn, that.loggedIn) &&
            Objects.equal(this.logoutUrl, that.logoutUrl) &&
            Objects.equal(this.loginUrl, that.loginUrl) &&
            Objects.equal(this.nickname, that.nickname);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(admin, email, loggedIn, logoutUrl, loginUrl, nickname);
    }
}
