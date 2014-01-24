package com.preferanser.client.restygwt;

import com.google.gwt.user.client.ui.DialogBox;
import com.google.inject.Inject;

public class DialogBoxRestyGwtListener extends RestyGwtRequestIdListenerAdapter {

    private final DialogBox dialogBox;

    @Inject
    public DialogBoxRestyGwtListener(@RequestProgress DialogBox dialogBox) {
        this.dialogBox = dialogBox;
    }

    @Override protected void beforeRequestIdSent(RequestIdValue requestIdValue) {
        dialogBox.center();
        dialogBox.show();
    }

    @Override protected void beforeResponseHandled(RequestIdValue requestIdValue) {
        dialogBox.hide();
    }

    @Override protected void beforeRequestIdErrorHandled(RequestIdValue requestIdValue) {
        dialogBox.hide();
    }

}