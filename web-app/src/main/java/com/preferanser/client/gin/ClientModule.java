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

package com.preferanser.client.gin;

import com.google.gwt.core.client.GWT;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.annotations.DefaultPlace;
import com.gwtplatform.mvp.client.annotations.ErrorPlace;
import com.gwtplatform.mvp.client.annotations.UnauthorizedPlace;
import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;
import com.gwtplatform.mvp.client.gin.DefaultModule;
import com.gwtplatform.mvp.client.proxy.DefaultPlaceManager;
import com.preferanser.client.application.ApplicationModule;
import com.preferanser.client.application.ResourceLoader;
import com.preferanser.client.application.i18n.I18nHelper;
import com.preferanser.client.place.NameTokens;
import com.preferanser.client.request.MyRequestFactory;
import com.preferanser.domain.GameBuilder;

public class ClientModule extends AbstractPresenterModule {

    @Override
    protected void configure() {
        install(new DefaultModule(DefaultPlaceManager.class));
        install(new ApplicationModule());

        // DefaultPlaceManager Places
        bindConstant().annotatedWith(DefaultPlace.class).to(NameTokens.GAME_EDITOR);
        bindConstant().annotatedWith(ErrorPlace.class).to(NameTokens.GAME_EDITOR); // TODO: define separate
        bindConstant().annotatedWith(UnauthorizedPlace.class).to(NameTokens.GAME_EDITOR);

        bind(I18nHelper.class).asEagerSingleton();

        bind(GameBuilder.class).toProvider(GameBuilderProvider.class).in(Singleton.class); // TODO: should be prototype scope
        bind(ResourceLoader.class).asEagerSingleton();
        bind(MyRequestFactory.class).toProvider(RequestFactoryProvider.class).in(Singleton.class);
    }

    static class GameBuilderProvider implements Provider<GameBuilder> {
        @Override public GameBuilder get() {
            GameBuilder gameBuilder = new GameBuilder();
            gameBuilder.setThreePlayers(); // TODO: remove this initialization
            return gameBuilder;
        }
    }

    static class RequestFactoryProvider implements Provider<MyRequestFactory> {
        private final MyRequestFactory requestFactory;

        @Inject
        public RequestFactoryProvider(EventBus eventBus) {
            requestFactory = GWT.create(MyRequestFactory.class);
            requestFactory.initialize(eventBus);
        }

        public MyRequestFactory get() {
            return requestFactory;
        }
    }
}
