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

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.preferanser.client.application.mvp.dialog.input.InputDialogPresenter;
import com.preferanser.client.application.mvp.editor.EditorPresenter;
import com.preferanser.client.application.mvp.editor.dialog.contract.ContractDialogPresenter;
import com.preferanser.client.application.mvp.editor.dialog.open.OpenDialogPresenter;
import com.preferanser.client.application.mvp.editor.dialog.validation.ValidationDialogPresenter;
import com.preferanser.shared.domain.Hand;
import com.preferanser.shared.domain.entity.Deal;
import com.preferanser.shared.domain.exception.validation.GameBuilderValidationError;

import java.util.Collection;
import java.util.List;

public class EditorDialogs {

    private final Provider<EditorPresenter> editorPresenterProvider;
    private final ContractDialogPresenter contractDialog;
    private final OpenDialogPresenter openDialog;
    private final InputDialogPresenter inputDialog;
    private final ValidationDialogPresenter validationDialog;

    @Inject
    public EditorDialogs(Provider<EditorPresenter> editorPresenterProvider,
                         ContractDialogPresenter contractDialog,
                         OpenDialogPresenter openDialog,
                         InputDialogPresenter inputDialog,
                         ValidationDialogPresenter validationDialog) {
        this.editorPresenterProvider = editorPresenterProvider;
        this.contractDialog = contractDialog;
        this.openDialog = openDialog;
        this.inputDialog = inputDialog;
        this.validationDialog = validationDialog;
    }

    public void showContractDialog(Hand hand) {
        EditorPresenter editorPresenter = editorPresenterProvider.get();
        contractDialog.setHand(hand);
        contractDialog.setHasHandContracts(editorPresenter);
        editorPresenter.addToPopupSlot(contractDialog);
    }

    public void showInputDialog(String title, String description) {
        inputDialog.setTitle(title);
        inputDialog.setDescription(description);
        inputDialog.onInputResult(new InputDialogPresenter.InputResultHandler() {
            @Override public void handleInputResult(String name) {
                if (!Strings.isNullOrEmpty(name)) {
                    inputDialog.getView().hide();
                    editorPresenterProvider.get().handleInputResult(name);
                }
            }
        });
        editorPresenterProvider.get().addToPopupSlot(inputDialog);
    }

    public void showValidationDialog(Collection<GameBuilderValidationError> errors) {
        validationDialog.setValidationErrors(errors);
        editorPresenterProvider.get().addToPopupSlot(validationDialog);
    }

    public void showOpenDialog(List<Deal> deals) {
        EditorPresenter editorPresenter = editorPresenterProvider.get();
        openDialog.setDeals(deals);
        openDialog.setEditorPresenter(editorPresenter);
        editorPresenter.addToPopupSlot(openDialog);
    }
}
