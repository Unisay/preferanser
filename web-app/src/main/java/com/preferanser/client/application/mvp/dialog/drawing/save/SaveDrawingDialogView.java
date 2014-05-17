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

package com.preferanser.client.application.mvp.dialog.drawing.save;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.PopupViewWithUiHandlers;
import com.preferanser.client.application.i18n.PreferanserConstants;
import com.preferanser.client.application.widgets.EscapableDialogBox;

public class SaveDrawingDialogView extends PopupViewWithUiHandlers<SaveDrawingDialogUiHandlers> implements SaveDrawingDialogPresenter.TheView {


    interface Binder extends UiBinder<PopupPanel, SaveDrawingDialogView> {}

    @UiField PreferanserConstants constants;
    @UiField EscapableDialogBox dialog;
    @UiField TextBox name;
    @UiField TextArea description;
    @UiField Button saveButton;

    private boolean updateButton = true;

    @Inject
    protected SaveDrawingDialogView(Binder uiBinder, EventBus eventBus) {
        super(eventBus);
        initWidget(uiBinder.createAndBindUi(this));
    }

    @UiHandler("saveButton") void onSaveDrawing(@SuppressWarnings("unused") ClickEvent event) {
        String nameText = name.getText();
        if (!nameText.isEmpty()) {
            String descriptionText = description.getText();
            getUiHandlers().save(nameText, descriptionText);
            hide();
        }
    }

    @Override public void show() {
        updateButton = true;
        Scheduler.get().scheduleFixedDelay(new Scheduler.RepeatingCommand() {
            @Override public boolean execute() {
                saveButton.setEnabled(name.getText().length() > 0);
                return updateButton;
            }
        }, 100);
        super.show();
    }

    @Override public void hide() {
        updateButton = false;
        name.setText("");
        description.setText("");
        super.hide();
    }
}