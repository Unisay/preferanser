package com.preferanser.client.application.table;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.PopupView;
import com.gwtplatform.mvp.client.PresenterWidget;

public class ContractDialogPresenterWidget extends PresenterWidget<ContractDialogPresenterWidget.MyView> {

    public interface MyView extends PopupView {
    }

    @Inject
    public ContractDialogPresenterWidget(EventBus eventBus, MyView view) {
        super(eventBus, view);

    }

}
