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

import com.preferanser.shared.domain.entity.User;

public class CurrentUserDto implements Dto {

    private Boolean isAdmin;
    private Boolean isLoggedIn;
    private User user;
    private String logoutUrl;
    private String loginUrl;
    private String nickname;

    public CurrentUserDto() {
        isAdmin = false;
        isLoggedIn = false;
        user = new User();
        loginUrl = "";
        logoutUrl = "";
    }

    public CurrentUserDto(Boolean isLoggedIn, User user) {
        this.isLoggedIn = isLoggedIn;
        this.user = user;
    }

    public Boolean isAdmin() {
        return isAdmin;
    }

    public Boolean isLoggedIn() {
        return isLoggedIn;
    }

    public User getUser() {
        return user;
    }

    public void setLogoutUrl(String logoutUrl) {
        this.logoutUrl = logoutUrl;
    }

    public String getLogoutUrl() {
        return logoutUrl;
    }

    public void setLoginUrl(String loginUrl) {
        this.loginUrl = loginUrl;
    }

    public String getLoginUrl() {
        return loginUrl;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }

    public void copyFrom(CurrentUserDto currentUser) {
        isAdmin = currentUser.isAdmin;
        isLoggedIn = currentUser.isLoggedIn;
        user = currentUser.user;
        logoutUrl = currentUser.logoutUrl;
        loginUrl = currentUser.loginUrl;
        nickname = currentUser.nickname;
    }

    public void setIsAdmin(Boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

}
