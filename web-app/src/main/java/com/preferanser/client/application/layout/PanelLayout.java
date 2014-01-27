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

package com.preferanser.client.application.layout;

import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Panel;
import com.preferanser.client.application.widgets.CardAnimation;
import com.preferanser.client.application.widgets.CardWidget;

import java.util.Collection;

public abstract class PanelLayout<T extends IsWidget> implements Layout<T> {

    private static final int ANIMATION_DURATION_MILLIS = 600;
    protected static final int PADDING = 10;
    protected final Panel panel;

    protected PanelLayout(Panel panel) {
        this.panel = panel;
    }

    @Override
    public final void apply(Collection<T> widgets) {
        if (panel.isVisible())
            layoutPanelWidgets(widgets);
    }

    protected abstract void layoutPanelWidgets(Collection<T> widgets);

    protected int getStartX() {
        return panel.getAbsoluteLeft() + PADDING;
    }

    protected int getStartY() {
        return panel.getAbsoluteTop() + PADDING;
    }

    protected int getWidth() {
        return panel.getOffsetWidth();
    }

    protected int getDisposableWidth() {
        return getWidth() - 2 * PADDING;
    }

    protected int getHeight() {
        return panel.getOffsetHeight();
    }

    protected int getDisposableHeight() {
        return getHeight() - 2 * PADDING;
    }

    protected void positionWidget(CardWidget cardWidget, int x, int y, int z) {
        Element element = cardWidget.getElement();
        int currentX = element.getOffsetLeft();
        int currentY = element.getOffsetTop();
        if (currentX != x || currentY != y) {
            CardAnimation animation = new CardAnimation(element);
            animation.moveTo(x, y, ANIMATION_DURATION_MILLIS);
        }
        element.getStyle().setZIndex(z);
    }

}
