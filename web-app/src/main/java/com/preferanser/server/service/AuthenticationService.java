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

package com.preferanser.server.service;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.common.base.Optional;
import com.google.inject.Provider;
import com.preferanser.server.dao.UserDao;
import com.preferanser.shared.domain.entity.User;
import com.preferanser.shared.dto.CurrentUserDto;

import javax.inject.Inject;

public class AuthenticationService implements Provider<CurrentUserDto> {

    private final UserService userService = UserServiceFactory.getUserService();
    private final UserDao userDao;

    @Inject
    public AuthenticationService(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public CurrentUserDto get() {
        boolean isLoggedIn = userService.isUserLoggedIn();

        CurrentUserDto currentUser = new CurrentUserDto(isLoggedIn, getUser());
        currentUser.logoutUrl = userService.createLogoutURL("/");
        currentUser.loginUrl = userService.createLoginURL("/");

        if (isLoggedIn) {
            currentUser.isAdmin = userService.isUserAdmin();
            currentUser.nickname = userService.getCurrentUser().getNickname();
        }

        return currentUser;
    }

    public User getUser() {
        boolean isLoggedIn = userService.isUserLoggedIn();

        User user = new User();
        if (isLoggedIn) {
            String googleId = userService.getCurrentUser().getUserId();

            user = userDao.findByGoogleId(googleId);
            if (user == null) {
                user = new User();
                user.setGoogleId(googleId);
                user = userDao.save(user);
            }
        }
        return user;
    }


    public Optional<String> getCurrentUserId() {
        if (!userService.isUserLoggedIn())
            return Optional.absent();
        else
            return Optional.of(userService.getCurrentUser().getUserId());
    }

}