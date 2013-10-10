package com.preferanser.client.application.table.layout;

import com.google.gwt.user.client.ui.Panel;

public abstract class PanelCardLayout extends CardLayoutBase {

    private final Panel panel;

    protected PanelCardLayout(Panel panel) {
        this.panel = panel;
    }

    @Override protected int getStartX() {
        return panel.getAbsoluteLeft() + 10;
    }

    @Override protected int getStartY() {
        return panel.getAbsoluteTop() + 10;
    }
}
