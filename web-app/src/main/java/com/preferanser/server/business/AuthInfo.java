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

package com.preferanser.server.business;

import com.google.appengine.api.users.User;

public class AuthInfo {

    private String nickname;
    private String email;
    private String loginURL;
    private String userId;

    @SuppressWarnings("unused")
    public AuthInfo() {
        // Default no-arg constructor required by RequestFactory
    }

    public AuthInfo(User user) {
        userId = user.getUserId();
        email = user.getEmail();
        nickname = user.getNickname();
    }

    public AuthInfo(String loginURL) {
        this.loginURL = loginURL;
    }

    @SuppressWarnings("unused")
    public boolean isAuthenticated() {
        return loginURL == null;
    }

    @SuppressWarnings("unused")
    public String getLoginURL() {
        return loginURL;
    }

    @SuppressWarnings("unused")
    public String getUserId() {
        return userId;
    }

    @SuppressWarnings("unused")
    public String getNickname() {
        return nickname;
    }

    @SuppressWarnings("unused")
    public String getEmail() {
        return email;
    }

}
