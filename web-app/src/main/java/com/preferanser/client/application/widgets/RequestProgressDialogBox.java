package com.preferanser.client.application.widgets;

import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Image;
import com.google.inject.Inject;
import com.preferanser.laf.client.PreferanserResources;

public class RequestProgressDialogBox extends DialogBox {

    @Inject
    public RequestProgressDialogBox(PreferanserResources resources) {
        super(false, true);
        setWidth("200px");
        setHeight("100px");
        setText("Waiting...");
        setAnimationEnabled(false);
        setGlassEnabled(true);
        Image image = new Image(resources.warp());
        add(image);
    }

}
