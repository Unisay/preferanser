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

package com.preferanser.client.application.mvp.editor.dialog;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.preferanser.client.application.mvp.editor.EditorPresenter;
import com.preferanser.client.application.mvp.editor.dialog.contract.ContractDialogPresenter;
import com.preferanser.client.application.mvp.editor.dialog.validation.ValidationDialogPresenter;
import com.preferanser.shared.domain.Hand;
import com.preferanser.shared.domain.exception.validation.GameBuilderValidationError;

import java.util.Collection;

public class EditorDialogs {

    private final Provider<EditorPresenter> editorPresenterProvider;
    private final ContractDialogPresenter contractDialog;
    private final ValidationDialogPresenter validationDialog;

    @Inject
    public EditorDialogs(Provider<EditorPresenter> editorPresenterProvider,
                         ContractDialogPresenter contractDialog,
                         ValidationDialogPresenter validationDialog) {
        this.editorPresenterProvider = editorPresenterProvider;
        this.contractDialog = contractDialog;
        this.validationDialog = validationDialog;
    }

    public void showContractDialog(Hand hand) {
        EditorPresenter editorPresenter = editorPresenterProvider.get();
        contractDialog.setHand(hand);
        contractDialog.setHasHandContracts(editorPresenter);
        editorPresenter.addToPopupSlot(contractDialog);
    }

    public void showValidationDialog(Collection<GameBuilderValidationError> errors) {
        validationDialog.setValidationErrors(errors);
        editorPresenterProvider.get().addToPopupSlot(validationDialog);
    }

}
