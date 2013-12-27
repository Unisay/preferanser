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

package com.preferanser.client.gwtp;

import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Bootstrapper;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.preferanser.client.service.AuthService;
import com.preferanser.client.service.Response;
import com.preferanser.shared.dto.CurrentUserDto;
import org.fusesource.restygwt.client.Method;

public class AuthBootstrapper implements Bootstrapper {

    private final PlaceManager placeManager;
    private final AuthService authService;
    private final CurrentUserDto currentUserDto;
    private final CurrentUserDtoResponse currentUserDtoResponse;

    @Inject
    public AuthBootstrapper(PlaceManager placeManager, AuthService authService, CurrentUserDto currentUserDto) {
        this.placeManager = placeManager;
        this.authService = authService;
        this.currentUserDto = currentUserDto;
        this.currentUserDtoResponse = new CurrentUserDtoResponse();
    }

    @Override public void onBootstrap() {
        authService.getCurrentUser(currentUserDtoResponse);
    }

    private class CurrentUserDtoResponse extends Response<CurrentUserDto> {

        @Override public void handle(CurrentUserDto response) {
            currentUserDto.copyFrom(response);
            placeManager.revealCurrentPlace();
        }

        @Override public void onFailure(Method method, Throwable exception) {
            super.onFailure(method, exception);
            placeManager.revealCurrentPlace();
        }
    }
}
