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

package com.preferanser.client.application;

import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;
import com.preferanser.client.application.mvp.auth.AuthModule;
import com.preferanser.client.application.mvp.deal.DealModule;
import com.preferanser.client.application.mvp.editor.EditorModule;
import com.preferanser.client.application.mvp.error.ErrorPresenter;
import com.preferanser.client.application.mvp.error.ErrorView;
import com.preferanser.client.application.mvp.main.MainModule;
import com.preferanser.client.application.mvp.player.PlayerModule;
import com.preferanser.client.application.mvp.unauthorized.UnauthorizedPresenter;
import com.preferanser.client.application.mvp.unauthorized.UnauthorizedView;

public class ApplicationModule extends AbstractPresenterModule {

    @Override
    @SuppressWarnings("OverlyCoupledMethod")
    protected void configure() {

        install(new AuthModule());
        install(new MainModule());
        install(new DealModule());
        install(new PlayerModule());
        install(new EditorModule());

        bindPresenter(
            UnauthorizedPresenter.class,
            UnauthorizedPresenter.TheView.class,
            UnauthorizedView.class,
            UnauthorizedPresenter.TheProxy.class
        );

        bindPresenter(
            ErrorPresenter.class,
            ErrorPresenter.TheView.class,
            ErrorView.class,
            ErrorPresenter.TheProxy.class
        );

        bindPresenter(
            ApplicationPresenter.class,
            ApplicationPresenter.TheView.class,
            ApplicationView.class,
            ApplicationPresenter.TheProxy.class
        );
    }

}
