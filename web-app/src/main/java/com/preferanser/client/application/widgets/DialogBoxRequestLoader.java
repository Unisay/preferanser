package com.preferanser.client.application.widgets;

import com.google.gwt.user.client.ui.DialogBox;
import com.google.inject.Inject;
import com.preferanser.laf.client.PreferanserResources;

public class DialogBoxRequestLoader extends DialogBox implements RequestLoader {

    @Inject
    public DialogBoxRequestLoader(PreferanserResources resources) {
        super(false, true);
        setWidth("200px");
        setHeight("100px");
        setText("Waiting...");
        setAnimationEnabled(false);
        setGlassEnabled(true);
        addStyleName(resources.css().requestLoader());
    }

    @Override public void startLoader() {
        center();
        show();
    }

    @Override public void stopLoader() {
        hide();
    }
}
