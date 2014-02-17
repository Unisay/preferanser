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

import com.google.gwt.inject.client.multibindings.GinMultibinder;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.gwtplatform.mvp.client.Bootstrapper;
import com.gwtplatform.mvp.client.annotations.DefaultPlace;
import com.gwtplatform.mvp.client.annotations.ErrorPlace;
import com.gwtplatform.mvp.client.annotations.UnauthorizedPlace;
import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;
import com.gwtplatform.mvp.client.gin.DefaultModule;
import com.preferanser.client.application.ApplicationModule;
import com.preferanser.client.application.ResourceLoader;
import com.preferanser.client.application.i18n.I18nHelper;
import com.preferanser.client.application.widgets.DialogBoxRequestLoader;
import com.preferanser.client.application.widgets.RequestLoader;
import com.preferanser.client.gwtp.AuthBootstrapper;
import com.preferanser.client.gwtp.NameTokens;
import com.preferanser.client.restygwt.ErrorHandlingRestyGwtRequestListener;
import com.preferanser.client.restygwt.RequestLoaderRestyGwtListener;
import com.preferanser.client.restygwt.RestyGwtDispatcher;
import com.preferanser.client.restygwt.RestyGwtRequestListener;
import com.preferanser.shared.domain.Editor;
import com.preferanser.shared.domain.User;

public class ClientModule extends AbstractPresenterModule {

    @Override
    protected void configure() {
        install(new DefaultModule());
        install(new ApplicationModule());

        // DefaultPlaceManager Places
        bindConstant().annotatedWith(DefaultPlace.class).to(NameTokens.DEALS);
        bindConstant().annotatedWith(ErrorPlace.class).to(NameTokens.ERROR);
        bindConstant().annotatedWith(UnauthorizedPlace.class).to(NameTokens.UNAUTHORIZED);

        bind(I18nHelper.class).asEagerSingleton();
        bind(Bootstrapper.class).to(AuthBootstrapper.class).in(Singleton.class);
        bind(User.class).asEagerSingleton();

        GinMultibinder<RestyGwtRequestListener> requestListenerMultibinder = GinMultibinder.newSetBinder(binder(), RestyGwtRequestListener.class);
//        requestListenerMultibinder.addBinding().to(RestyGwtLoggingRequestListener.class);
        requestListenerMultibinder.addBinding().to(RequestLoaderRestyGwtListener.class);
        requestListenerMultibinder.addBinding().to(ErrorHandlingRestyGwtRequestListener.class);

//        bind(RequestLoader.class).to(StatusMessageRequestLoader.class).in(Singleton.class);
        bind(RequestLoader.class).to(DialogBoxRequestLoader.class).in(Singleton.class);
        bind(RestyGwtDispatcher.class).toProvider(RestyGwtDispatcher.Provider.class).asEagerSingleton();
        bind(Editor.class).toProvider(GameBuilderProvider.class).in(Singleton.class);
        bind(ResourceLoader.class).asEagerSingleton();
    }

    static class GameBuilderProvider implements Provider<Editor> {
        @Override public Editor get() {
            Editor editor = new Editor();
            editor.setThreePlayers(); // TODO: remove this initialization
            return editor;
        }
    }

}
