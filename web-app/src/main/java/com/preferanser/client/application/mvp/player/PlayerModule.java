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

package com.preferanser.client.application.mvp.player;

import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;
import com.preferanser.client.application.mvp.dialog.drawing.DrawingsDialogPresenter;
import com.preferanser.client.application.mvp.dialog.drawing.DrawingsDialogView;
import com.preferanser.client.application.mvp.dialog.drawing.SaveDrawingDialogPresenter;
import com.preferanser.client.application.mvp.dialog.drawing.SaveDrawingDialogView;

/**
 * Gin module for the mvp page
 */
public class PlayerModule extends AbstractPresenterModule {

    @Override
    protected void configure() {
        bindSingletonPresenterWidget(SaveDrawingDialogPresenter.class, SaveDrawingDialogPresenter.TheView.class, SaveDrawingDialogView.class);
        bindSingletonPresenterWidget(DrawingsDialogPresenter.class, DrawingsDialogPresenter.TheView.class, DrawingsDialogView.class);
        bindPresenter(PlayerPresenter.class, PlayerView.class, PlayerViewImpl.class, PlayerPresenter.Proxy.class);
    }

}
