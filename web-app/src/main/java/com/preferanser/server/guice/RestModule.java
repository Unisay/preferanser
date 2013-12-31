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

package com.preferanser.server.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.servlet.RequestScoped;
import com.preferanser.server.resource.AuthenticationService;
import com.preferanser.shared.dto.CurrentUserDto;
import com.sun.jersey.api.core.PackagesResourceConfig;

import javax.validation.Validator;

public class RestModule extends AbstractModule {

    @Override protected void configure() {
        bind(Validator.class).toProvider(HibernateValidatorProvider.class).in(Singleton.class);
        for (Class<?> resource : new PackagesResourceConfig("com.preferanser.server.resource").getClasses())
            bind(resource).in(Singleton.class);
        bind(CurrentUserDto.class).toProvider(AuthenticationService.class).in(RequestScoped.class);
    }

}
