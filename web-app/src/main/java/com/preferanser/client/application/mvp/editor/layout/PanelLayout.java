/*
 * Preferanser is a program to simulate and calculate Russian Preferans Card game deals.
 *
 *     Copyright (C) 2013  Yuriy Lazarev <Yuriy.Lazarev@gmail.com>
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see [http://www.gnu.org/licenses/].
 */

package com.preferanser.client.application.mvp.editor.layout;

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
