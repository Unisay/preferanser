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

package com.preferanser.client.application.mvp.dialog.input;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.PopupViewWithUiHandlers;
import com.preferanser.client.application.widgets.EscapableDialogBox;

public class InputDialogView extends PopupViewWithUiHandlers<InputDialogUiHandlers> implements InputDialogPresenter.TheView {

    interface Binder extends UiBinder<PopupPanel, InputDialogView> {}

    @UiField
    EscapableDialogBox dialog;

    @UiField
    Label descriptionLabel;

    @UiField
    TextBox textBox;

    @UiField
    Button button;

    @Inject
    protected InputDialogView(Binder uiBinder, EventBus eventBus) {
        super(eventBus);
        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public void setTitle(String title) {
        dialog.setText(title);
    }

    @Override
    public void setDescription(String description) {
        descriptionLabel.setText(description);
    }

    @UiHandler("button")
    void onEnter(@SuppressWarnings("unused") ClickEvent event) {
        String value = textBox.getValue();
        textBox.setValue("");
        getUiHandlers().save(value);
    }

}
