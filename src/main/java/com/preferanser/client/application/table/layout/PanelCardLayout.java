package com.preferanser.client.application.table.layout;

import com.google.gwt.user.client.ui.Panel;

public abstract class PanelCardLayout extends CardLayoutBase {

    protected static final int PADDING = 10;
    private final Panel panel;

    protected PanelCardLayout(Panel panel) {
        this.panel = panel;
    }

    @Override
    protected int getStartX() {
        return PADDING;
    }

    @Override
    protected int getStartY() {
        return PADDING;
    }

    protected int getWidth() {
        return panel.getOffsetWidth();
    }

    protected int getDisposableWidth() {
        return getWidth() - PADDING - PADDING;
    }

    protected int getHeight() {
        return panel.getOffsetHeight();
    }

    protected int getDisposableHeight() {
        return getHeight() - 4 * PADDING;
    }
}
