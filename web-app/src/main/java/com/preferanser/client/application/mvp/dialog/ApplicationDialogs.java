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

package com.preferanser.client.application.mvp.dialog;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.gwtplatform.mvp.client.PopupView;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.proxy.RevealRootPopupContentEvent;
import com.preferanser.client.application.mvp.dialog.contract.ContractDialogPresenter;
import com.preferanser.client.application.mvp.dialog.drawing.open.OpenDrawingDialogPresenter;
import com.preferanser.client.application.mvp.dialog.drawing.save.SaveDrawingDialogPresenter;
import com.preferanser.client.application.mvp.dialog.validation.ValidationDialogPresenter;
import com.preferanser.client.application.mvp.main.MainPresenter;
import com.preferanser.shared.domain.Drawing;
import com.preferanser.shared.domain.Hand;
import com.preferanser.shared.domain.exception.validation.EditorValidationError;

import java.util.Collection;
import java.util.List;

public class ApplicationDialogs {

    private final Provider<MainPresenter> mainPresenterProvider;
    private final ContractDialogPresenter contractDialog;
    private final ValidationDialogPresenter validationDialog;
    private final SaveDrawingDialogPresenter saveDrawingDialog;
    private final OpenDrawingDialogPresenter openDrawingDialog;

    @Inject
    public ApplicationDialogs(
        Provider<MainPresenter> mainPresenterProvider,
        ContractDialogPresenter contractDialog,
        ValidationDialogPresenter validationDialog,
        SaveDrawingDialogPresenter saveDrawingDialog,
        OpenDrawingDialogPresenter openDrawingDialog
    ) {
        this.mainPresenterProvider = mainPresenterProvider;
        this.contractDialog = contractDialog;
        this.validationDialog = validationDialog;
        this.saveDrawingDialog = saveDrawingDialog;
        this.openDrawingDialog = openDrawingDialog;
    }

    public void showContractDialog(Hand hand, HandContractSetter handContractSetter) {
        contractDialog.setHand(hand);
        contractDialog.setHandContractSetter(handContractSetter);
        showGlobalDialog(contractDialog);
    }

    public void showValidationDialog(Collection<EditorValidationError> errors) {
        validationDialog.setValidationErrors(errors);
        showGlobalDialog(validationDialog);
    }

    public void showSaveDrawingDialog(NameDescriptionSetter nameDescriptionSetter) {
        saveDrawingDialog.setNameDescriptionSetter(nameDescriptionSetter);
        showGlobalDialog(saveDrawingDialog);
    }

    public void showOpenDrawingDialog(List<Drawing> drawingList, DrawingSetter drawingSetter) {
        openDrawingDialog.setDrawings(drawingList);
        openDrawingDialog.setDrawingSetter(drawingSetter);
        showGlobalDialog(openDrawingDialog);
    }

    private void showGlobalDialog(PresenterWidget<? extends PopupView> dialog) {
        RevealRootPopupContentEvent.fire(mainPresenterProvider.get(), dialog);
    }
}
