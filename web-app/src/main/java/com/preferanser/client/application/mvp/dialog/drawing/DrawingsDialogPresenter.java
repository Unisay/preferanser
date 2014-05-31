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

package com.preferanser.client.application.mvp.dialog.drawing;

import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PopupView;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.preferanser.client.application.mvp.event.DrawingDeleteEvent;
import com.preferanser.client.application.mvp.event.DrawingOpenEvent;
import com.preferanser.client.gwtp.NameTokens;
import com.preferanser.shared.domain.Drawing;

import java.util.List;

public class DrawingsDialogPresenter extends PresenterWidget<DrawingsDialogPresenter.TheView> implements DrawingsDialogUiHandlers {

    private List<Drawing> drawings;
    private final PlaceManager placeManager;

    public interface TheView extends PopupView, HasUiHandlers<DrawingsDialogUiHandlers> {
        void displayDrawings(List<Drawing> drawings);
        void displayLink(String link);
    }

    @Inject public DrawingsDialogPresenter(EventBus eventBus, TheView view, PlaceManager placeManager) {
        super(eventBus, view);
        this.placeManager = placeManager;
        getView().setUiHandlers(this);
    }

    @Override public void open(Drawing drawing) {
        DrawingOpenEvent.fire(this, drawing);
    }

    @Override public void delete(Drawing drawing) {
        assert drawings.contains(drawing);
        drawings.remove(drawing);
        if (drawings.isEmpty()) {
            getView().hide();
        } else {
            getView().displayDrawings(drawings);
        }
        DrawingDeleteEvent.fire(this, drawing);
    }

    @Override public void select(Drawing drawing) {
        PlaceRequest placeRequest = new PlaceRequest.Builder()
            .nameToken(NameTokens.PLAYER)
            .with("user", "" + drawing.getUserId())
            .with("deal", "" + drawing.getDealId())
            .with("drawing", "" + drawing.getId())
            .build();
        String historyToken = placeManager.buildHistoryToken(placeRequest);
        UrlBuilder urlBuilder = new UrlBuilder()
            .setProtocol(Window.Location.getProtocol())
            .setHost(Window.Location.getHost())
            .setPath(Window.Location.getPath())
            .setHash(historyToken);
        String port = Window.Location.getPort();
        if (port != null && port.length() > 1) {
            urlBuilder.setPort(Integer.parseInt(port));
        }
        getView().displayLink(urlBuilder.buildString());
    }

    public void setDrawings(List<Drawing> drawings) {
        this.drawings = drawings;
    }

    @Override protected void onReveal() {
        getView().displayDrawings(drawings);
    }
}