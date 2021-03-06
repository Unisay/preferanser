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

package com.preferanser.client.application.mvp;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.preferanser.client.application.widgets.CardWidget;
import com.preferanser.client.geom.Point;

public class ImageDragController implements MouseMoveHandler, MouseUpHandler {

    private CardWidget cardWidget;
    private Point clickOffset;
    private boolean down = false;
    private boolean drag = false;
    private final Document doc;
    private final String cardDraggingClassName;

    public ImageDragController(Document doc, String cardDraggingClassName) {
        this.doc = doc;
        this.cardDraggingClassName = cardDraggingClassName;
    }

    public void onCardWidgetMouseDown(CardWidget cardWidget, MouseDownEvent event) {
        if (cardWidget.isDraggable()) {
            down = true;
            this.clickOffset = Point.FromMouseEventRelative(event);
            this.cardWidget = cardWidget;
        }
    }

    @Override public void onMouseMove(MouseMoveEvent event) {
        if (down) {
            // noinspection GWTStyleCheck
            cardWidget.addStyleName(cardDraggingClassName);
            drag = true;
        }
        if (drag)
            updatePosition(Point.FromMouseEvent(event, doc).minus(clickOffset));
    }

    @Override public void onMouseUp(MouseUpEvent event) {
        if (drag) {
            // noinspection GWTStyleCheck
            cardWidget.removeStyleName(cardDraggingClassName);
            this.cardWidget = null;
            drag = false;
        }
        down = false;
    }

    private void updatePosition(Point point) {
        Style style = cardWidget.getElement().getStyle();
        style.setLeft(point.getX(), Style.Unit.PX);
        style.setTop(point.getY(), Style.Unit.PX);
    }

    public boolean isDrag() {
        return drag;
    }

    public CardWidget getCardWidget() {
        return cardWidget;
    }
}
