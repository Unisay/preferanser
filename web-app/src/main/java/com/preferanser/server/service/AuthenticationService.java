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
import com.google.common.base.Optional;
import com.google.inject.Provider;
import com.preferanser.server.dao.UserDao;
import com.preferanser.server.entity.UserEntity;
import com.preferanser.server.exception.NoAuthenticatedUserException;
import com.preferanser.shared.domain.User;

import javax.inject.Inject;

public class AuthenticationService implements Provider<User> {

    private final UserDao userDao;
    private final UserService userService;
    private DealService dealService;

    @Inject
    public AuthenticationService(Provider<UserService> userServiceProvider, DealService dealService, UserDao userDao) {
        this.dealService = dealService;
        this.userService = userServiceProvider.get();
        this.userDao = userDao;
    }

    @Override
    public User get() {
        Optional<UserEntity> userEntityOptional = getCurrentUser();

        boolean isLoggedIn = userService.isUserLoggedIn();
        String logoutURL = userService.createLogoutURL("/");
        String loginUrl = userService.createLoginURL("/");
        boolean admin = isLoggedIn && userService.isUserAdmin();
        com.google.appengine.api.users.User currentUser = userService.getCurrentUser();
        String nickname = isLoggedIn ? currentUser.getNickname() : null;
        String email = userEntityOptional.isPresent() ? userEntityOptional.get().getEmail() : null;
        return new User(admin, isLoggedIn, logoutURL, loginUrl, email, nickname);
    }

    public Optional<UserEntity> getCurrentUser() {
        if (!userService.isUserLoggedIn())
            return Optional.absent();

        com.google.appengine.api.users.User currentUser = userService.getCurrentUser();
        String googleId = currentUser.getUserId();

        Optional<UserEntity> userOptional = userDao.findById(googleId);
        if (userOptional.isPresent()) {
            return userOptional;
        } else {
            UserEntity user = new UserEntity();
            user.setId(googleId);
            user.setEmail(currentUser.getEmail());
            user.setAdmin(userService.isUserAdmin());
            user = userDao.save(user);
            dealService.importSharedDeals(user);
            return Optional.of(user);
        }
    }

    public UserEntity getCurrentUserOrThrow() {
        Optional<UserEntity> currentUserOptional = getCurrentUser();

        if (!currentUserOptional.isPresent())
            throw new NoAuthenticatedUserException();

        return currentUserOptional.get();
    }
}