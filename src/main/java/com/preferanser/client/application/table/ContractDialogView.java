package com.preferanser.client.application.table;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.PopupViewImpl;

public class ContractDialogView extends PopupViewImpl implements ContractDialogPresenterWidget.MyView {

    interface Binder extends UiBinder<PopupPanel, ContractDialogView> {}

    @UiField
    Button okButton;

    @Inject
    protected ContractDialogView(Binder uiBinder, EventBus eventBus) {
        super(eventBus);
        initWidget(uiBinder.createAndBindUi(this));
    }

    @UiHandler("okButton") void okButtonClicked(@SuppressWarnings("unused") ClickEvent event) {
        hide();
    }
}
