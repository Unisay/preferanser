package com.preferanser.client.application.widgets;

import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Image;
import com.google.inject.Inject;
import com.preferanser.client.application.i18n.PreferanserConstants;
import com.preferanser.laf.client.PreferanserResources;

public class DialogBoxRequestLoader extends DialogBox implements RequestLoader {

    @Inject
    public DialogBoxRequestLoader(PreferanserResources resources, PreferanserConstants constants) {
        super(false, true);
        setWidth("200px");
        setHeight("100px");
        setText(constants.waiting());
        setAnimationEnabled(false);
        setGlassEnabled(true);
        addStyleName(resources.style().requestLoader());
        Image image = new Image(resources.sandClock());
        image.addStyleName(resources.style().loadingImage());
        add(image);
    }

    @Override public void startLoader() {
        center();
        show();
    }

    @Override public void stopLoader() {
        hide();
    }
}
