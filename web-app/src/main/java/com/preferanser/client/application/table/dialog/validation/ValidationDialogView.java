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

package com.preferanser.client.application.table.dialog.validation;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.PopupViewWithUiHandlers;
import com.preferanser.client.application.i18n.PreferanserConstants;
import com.preferanser.client.application.widgets.EscapableDialogBox;
import com.preferanser.domain.Game;

import java.util.Collection;

public class ValidationDialogView extends PopupViewWithUiHandlers<ValidationDialogUiHandlers> implements ValidationDialogPresenter.MyView {

    private final PreferanserConstants constants;

    interface Binder extends UiBinder<PopupPanel, ValidationDialogView> {}

    @UiField EscapableDialogBox dialog;
    @UiField VerticalPanel verticalPanel;
    @UiField ValidationDialogStyle style;

    @Inject
    protected ValidationDialogView(Binder uiBinder, EventBus eventBus, PreferanserConstants constants) {
        super(eventBus);
        this.constants = constants;
        initWidget(uiBinder.createAndBindUi(this));
        dialog.setText("Ошибки");
    }

    @Override
    public void displayValidationErrors(Collection<Game.ValidationError> validationErrors) {
        verticalPanel.clear();
        for (Game.ValidationError validationError : validationErrors)
            displayValidationError(constants.getString(validationError.name()));
    }

    private void displayValidationError(String errorMessage) {
        Label errorLabel = GWT.create(Label.class);
        errorLabel.setText(errorMessage);
        errorLabel.setStylePrimaryName(style.errorLabel());
        verticalPanel.add(errorLabel);
    }
}
