package com.preferanser.client.application.table.layout;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Panel;
import com.preferanser.client.application.widgets.CardWidget;

public abstract class PanelLayout<T extends IsWidget> implements Layout<T> {

    protected static final int PADDING = 10;
    private final Panel panel;

    protected PanelLayout(Panel panel) {
        this.panel = panel;
    }

    protected int getStartX() {
        return PADDING;
    }

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

    protected void positionWidget(CardWidget cardWidget, int x, int y, int z) {
        Style style = cardWidget.getElement().getStyle();
        style.setLeft(x, Style.Unit.PX);
        style.setTop(y, Style.Unit.PX);
        style.setZIndex(z);
    }
}
