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

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Scopes;
import com.google.inject.servlet.GuiceServletContextListener;
import com.sun.jersey.guice.JerseyServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;
import com.sun.jersey.spi.container.servlet.WebComponent;

public class PreferanserServletContextListener extends GuiceServletContextListener {

    @Override protected Injector getInjector() {
        return Guice.createInjector(new GuiceServletModule(), new RestModule());
    }

    private static class GuiceServletModule extends JerseyServletModule {
        @Override protected void configureServlets() {
            bind(GuiceContainer.class);
            bind(JacksonJsonProvider.class).toProvider(JacksonJsonProviderProvider.class).in(Scopes.SINGLETON);
            serve("/Preferanser/*").with(GuiceContainer.class, ImmutableMap.of(
                    WebComponent.RESOURCE_CONFIG_CLASS, JerseyResourceConfig.class.getName()));
        }
    }

}
