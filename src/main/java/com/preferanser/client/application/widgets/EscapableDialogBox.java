package com.preferanser.client.application.widgets;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.DialogBox;

/**
 * Dialog Box widget that closes itself when ESCAPE pressed
 */
public class EscapableDialogBox extends DialogBox {

    @Override
    protected void onPreviewNativeEvent(Event.NativePreviewEvent event) {
        if (!event.isCanceled()
                && event.getTypeInt() == Event.ONKEYDOWN
                && event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ESCAPE) {
            this.hide();
        }
        super.onPreviewNativeEvent(event);
    }

}
