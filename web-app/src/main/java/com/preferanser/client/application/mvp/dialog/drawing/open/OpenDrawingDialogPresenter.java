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

package com.preferanser.client.application.mvp.dialog.drawing.open;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PopupView;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.preferanser.client.application.mvp.event.DrawingDeleteEvent;
import com.preferanser.client.application.mvp.event.DrawingOpenEvent;
import com.preferanser.shared.domain.Drawing;

import java.util.List;

public class OpenDrawingDialogPresenter extends PresenterWidget<OpenDrawingDialogPresenter.TheView> implements OpenDrawingDialogUiHandlers {

    private List<Drawing> drawings;

    public interface TheView extends PopupView, HasUiHandlers<OpenDrawingDialogUiHandlers> {
        void displayDrawings(List<Drawing> drawings);
    }

    @Inject public OpenDrawingDialogPresenter(EventBus eventBus, TheView view) {
        super(eventBus, view);
        getView().setUiHandlers(this);
    }

    @Override public void open(Drawing drawing) {
        DrawingOpenEvent.fire(this, drawing);
    }

    @Override public void delete(Drawing drawing) {
        assert drawings.contains(drawing);
        drawings.remove(drawing);
        getView().displayDrawings(drawings);
        DrawingDeleteEvent.fire(this, drawing);
    }

    public void setDrawings(List<Drawing> drawings) {
        this.drawings = drawings;
    }

    @Override protected void onReveal() {
        getView().displayDrawings(drawings);
    }
}