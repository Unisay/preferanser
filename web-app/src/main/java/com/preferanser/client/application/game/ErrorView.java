package com.preferanser.client.application.game;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.gwtplatform.mvp.client.ViewImpl;

public class ErrorView extends ViewImpl implements ErrorPresenter.ErrorView {

    public ErrorView() {
        FlowPanel panel = new FlowPanel();
        panel.add(new Label("ERROR"));
        initWidget(panel);
    }

}
