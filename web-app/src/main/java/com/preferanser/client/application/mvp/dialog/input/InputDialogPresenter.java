package com.preferanser.client.application.mvp.dialog.input;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PopupView;
import com.gwtplatform.mvp.client.PresenterWidget;

public class InputDialogPresenter extends PresenterWidget<InputDialogPresenter.TheView> implements InputDialogUiHandlers {

    private InputResultHandler inputResultHandler;

    @Inject
    public InputDialogPresenter(EventBus eventBus, TheView view) {
        super(eventBus, view);
        getView().setUiHandlers(this);
    }

    public interface TheView extends PopupView, HasUiHandlers<InputDialogUiHandlers> {
        void setTitle(String title);
        void setDescription(String description);
    }

    public void setTitle(String title) {
        getView().setTitle(title);
    }

    public void setDescription(String description) {
        getView().setDescription(description);
    }

    @Override
    public void save(String name) {
        inputResultHandler.handleInputResult(name);
    }

    public void onInputResult(InputResultHandler inputResultHandler) {
        this.inputResultHandler = inputResultHandler;
    }

    public static interface InputResultHandler {
        public void handleInputResult(String name);
    }
}
