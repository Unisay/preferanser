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
import com.preferanser.shared.domain.entity.BaseEntity;
import com.preferanser.shared.domain.entity.User;

public class CurrentUserDto extends BaseEntity implements Dto {

    public Boolean isAdmin;
    public Boolean isLoggedIn;
    public User user;
    public String logoutUrl;
    public String loginUrl;
    public String nickname;

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

    public void copyFrom(CurrentUserDto currentUser) {
        isAdmin = currentUser.isAdmin;
        isLoggedIn = currentUser.isLoggedIn;
        user = currentUser.user;
        logoutUrl = currentUser.logoutUrl;
        loginUrl = currentUser.loginUrl;
        nickname = currentUser.nickname;
    }

    @Override public String toString() {
        return Objects.toStringHelper(this)
            .add("isAdmin", isAdmin)
            .add("isLoggedIn", isLoggedIn)
            .add("user", user)
            .add("logoutUrl", logoutUrl)
            .add("loginUrl", loginUrl)
            .add("nickname", nickname)
            .toString();
    }
}
