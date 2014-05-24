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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.PopupViewWithUiHandlers;
import com.preferanser.client.application.i18n.PreferanserConstants;
import com.preferanser.client.application.widgets.EscapableDialogBox;
import com.preferanser.shared.domain.Drawing;

import java.util.List;

public class OpenDrawingDialogView extends PopupViewWithUiHandlers<OpenDrawingDialogUiHandlers> implements OpenDrawingDialogPresenter.TheView {

    public static final DateTimeFormat DATE_TIME_FORMAT = DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_TIME_SHORT);

    interface Binder extends UiBinder<PopupPanel, OpenDrawingDialogView> {}

    @UiField PreferanserConstants constants;
    @UiField EscapableDialogBox dialog;
    @UiField ListBox listBox;

    private List<Drawing> drawings;

    @Inject
    protected OpenDrawingDialogView(Binder uiBinder, EventBus eventBus) {
        super(eventBus);
        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override public void displayDrawings(List<Drawing> drawings) {
        this.drawings = drawings;
        listBox.clear();
        for (Drawing drawing : drawings) {
            listBox.addItem(drawing.getName() + " (" + DATE_TIME_FORMAT.format(drawing.getCreatedAt()) + ")");
        }
    }

    @UiHandler("openButton") void onOpen(@SuppressWarnings("unused") ClickEvent event) {
        getUiHandlers().open(drawings.get(listBox.getSelectedIndex()));
        hide();
    }

    @UiHandler("cancelButton") void onCancel(@SuppressWarnings("unused") ClickEvent event) {
        hide();
    }

    @UiHandler("deleteButton") void onDelete(@SuppressWarnings("unused") ClickEvent event) {
        getUiHandlers().delete(drawings.get(listBox.getSelectedIndex()));
    }

}