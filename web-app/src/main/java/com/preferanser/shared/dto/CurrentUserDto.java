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

package com.preferanser.shared.dto;

import com.google.common.base.Objects;
import com.preferanser.shared.domain.entity.User;

@SuppressWarnings("unused")
public class CurrentUserDto implements Dto {

    public Boolean admin;
    public Boolean loggedIn;
    public User user;
    public String logoutUrl;
    public String loginUrl;
    public String nickname;

    public CurrentUserDto() {
        admin = false;
        loggedIn = false;
        user = new User();
        loginUrl = "";
        logoutUrl = "";
    }

    public CurrentUserDto(Boolean loggedIn, User user) {
        this.loggedIn = loggedIn;
        this.user = user;
    }

    public void copyFrom(CurrentUserDto currentUser) {
        admin = currentUser.admin;
        loggedIn = currentUser.loggedIn;
        user = currentUser.user;
        logoutUrl = currentUser.logoutUrl;
        loginUrl = currentUser.loginUrl;
        nickname = currentUser.nickname;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    @Override public String toString() {
        return Objects.toStringHelper(this)
            .add("getAdmin", admin)
            .add("loggedIn", loggedIn)
            .add("user", user)
            .add("logoutUrl", logoutUrl)
            .add("loginUrl", loginUrl)
            .add("nickname", nickname)
            .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CurrentUserDto that = (CurrentUserDto) o;

        return Objects.equal(this.admin, that.admin) &&
            Objects.equal(this.loggedIn, that.loggedIn) &&
            Objects.equal(this.user, that.user) &&
            Objects.equal(this.logoutUrl, that.logoutUrl) &&
            Objects.equal(this.loginUrl, that.loginUrl) &&
            Objects.equal(this.nickname, that.nickname);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(admin, loggedIn, user, logoutUrl, loginUrl, nickname);
    }
}
