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

package com.preferanser.server.service.impl;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.common.base.Preconditions;
import com.preferanser.server.business.AuthInfo;
import com.preferanser.server.service.PreferanserUserService;
import com.preferanser.server.service.exception.NoAuthenticatedUserException;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;

@Service("userService")
public class AppengineUserService implements PreferanserUserService {

    private final UserService userService;

    public AppengineUserService() {
        userService = UserServiceFactory.getUserService();
        Preconditions.checkNotNull(userService, "UserService not available");
    }

    @Override public AuthInfo getAuthInfo() {
        User user = userService.getCurrentUser();
        if (user == null) {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            String pageUrl = request.getHeader("pageurl");
            return new AuthInfo(userService.createLoginURL(pageUrl));
        }
        return new AuthInfo(user);
    }

    @Override public String getCurrentUserId() throws NoAuthenticatedUserException {
        User user = userService.getCurrentUser();
        if (user == null)
            throw new NoAuthenticatedUserException();
        return user.getUserId();
    }

}
