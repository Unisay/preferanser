package com.preferanser.client.application.table.layout;

import com.google.gwt.user.client.ui.Panel;

public abstract class PanelCardLayout extends CardLayoutBase {

    protected PanelCardLayout(Panel panel) {
        super(panel.getAbsoluteLeft(), panel.getAbsoluteTop(), 0);
    }

}
