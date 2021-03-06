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

package com.preferanser.client.application.mvp.dialog.validation;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.PopupViewWithUiHandlers;
import com.preferanser.client.application.i18n.PreferanserConstants;
import com.preferanser.client.application.i18n.PreferanserMessages;
import com.preferanser.client.application.widgets.EscapableDialogBox;
import com.preferanser.client.application.widgets.UlListPanel;
import com.preferanser.shared.domain.exception.validation.EditorValidationError;

import java.util.Collection;

public class ValidationDialogView extends PopupViewWithUiHandlers<ValidationDialogUiHandlers> implements ValidationDialogPresenter.TheView {

    private final PreferanserMessages messages;

    interface Binder extends UiBinder<PopupPanel, ValidationDialogView> {}

    @UiField UlListPanel listPanel;
    @UiField PreferanserConstants constants;
    @UiField EscapableDialogBox dialog;

    @Inject
    protected ValidationDialogView(Binder uiBinder, EventBus eventBus, PreferanserMessages messages) {
        super(eventBus);
        this.messages = messages;
        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public void displayValidationErrors(Collection<EditorValidationError> validationErrors) {
        listPanel.clear();
        for (EditorValidationError validationError : validationErrors)
            displayValidationError(validationError.formatLocalMessage(constants, messages));
    }

    private void displayValidationError(String errorMessage) {
        Label errorLabel = GWT.create(Label.class);
        errorLabel.setText(errorMessage);
        listPanel.add(errorLabel);
    }
}
