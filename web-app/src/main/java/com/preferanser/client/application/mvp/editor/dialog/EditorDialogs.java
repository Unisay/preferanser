package com.preferanser.client.application.mvp.editor.dialog;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.preferanser.client.application.mvp.dialog.input.InputDialogPresenter;
import com.preferanser.client.application.mvp.editor.EditorPresenter;
import com.preferanser.client.application.mvp.editor.dialog.contract.ContractDialogPresenter;
import com.preferanser.client.application.mvp.editor.dialog.open.OpenDialogPresenter;
import com.preferanser.client.application.mvp.editor.dialog.validation.ValidationDialogPresenter;
import com.preferanser.shared.domain.Cardinal;
import com.preferanser.shared.domain.GameBuilder;
import com.preferanser.shared.domain.entity.Deal;

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

    public void showContractDialog(Cardinal cardinal) {
        EditorPresenter editorPresenter = editorPresenterProvider.get();
        contractDialog.setCardinal(cardinal);
        contractDialog.setHasCardinalContracts(editorPresenter);
        editorPresenter.addToPopupSlot(contractDialog);
    }

    public void showInputDialog(String title, String description) {
        EditorPresenter editorPresenter = editorPresenterProvider.get();
        inputDialog.setTitle(title);
        inputDialog.setDescription(description);
        inputDialog.onInputResult(editorPresenter);
        editorPresenter.addToPopupSlot(inputDialog);
    }

    public void hideInputDialog() {
        inputDialog.getView().hide();
    }

    public void showValidationDialog(Collection<GameBuilder.Error> errors) {
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
