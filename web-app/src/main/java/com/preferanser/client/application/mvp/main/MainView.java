package com.preferanser.client.application.mvp.main;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;

public class MainView extends ViewWithUiHandlers<MainUiHandlers> implements MainPresenter.MainView {

    public interface Binder extends UiBinder<Widget, MainView> {}

    @UiField SimplePanel main;
    @UiField Button editorButton;

    @Inject
    public MainView(Binder uiBinder) {
        initWidget(uiBinder.createAndBindUi(this));
    }

    @UiHandler("editorButton") void onEditor(@SuppressWarnings("unused") ClickEvent event) {
        getUiHandlers().openDealEditor();
    }

    @Override
    public void setInSlot(Object slot, IsWidget content) {
        if (slot == MainPresenter.MAIN_SLOT) {
            main.setWidget(content);
        } else {
            super.setInSlot(slot, content);
        }
    }

}
