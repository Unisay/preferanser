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

package com.preferanser.client.application.widgets;

import com.google.gwt.event.dom.client.*;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;
import com.preferanser.shared.domain.Card;

public class CardWidget extends Composite {

    public interface Handlers {
        void onCardMouseDown(CardWidget cardWidget, MouseDownEvent event);
        void onCardDragStart(CardWidget cardWidget, DragStartEvent event);
        void onCardDoubleClick(CardWidget cardWidget, DoubleClickEvent event);
    }

    private final Card card;
    private final SimplePanel wrapper;
    private final Image image;
    private final String disabledClass;
    private final String draggableClass;
    private boolean draggable;
    private boolean disabled;

    public CardWidget(Card card, String disabledClass, String draggableClass) {
        this.card = card;
        this.disabledClass = disabledClass;
        this.draggableClass = draggableClass;
        image = new Image();
        wrapper = new SimplePanel(image);
        initWidget(wrapper);
        setDraggable(true);
        setDisabled(false);
    }

    public void setResource(ImageResource imageResource) {
        image.setResource(imageResource);
    }

    public void setHandlers(final Handlers handlers) {
        image.addMouseDownHandler(new MouseDownHandler() {
            @Override public void onMouseDown(MouseDownEvent event) {
                handlers.onCardMouseDown(CardWidget.this, event);
            }
        });
        image.addDragStartHandler(new DragStartHandler() {
            @Override public void onDragStart(DragStartEvent event) {
                handlers.onCardDragStart(CardWidget.this, event);
            }
        });
        image.addDoubleClickHandler(new DoubleClickHandler() {
            @Override public void onDoubleClick(DoubleClickEvent event) {
                handlers.onCardDoubleClick(CardWidget.this, event);
            }
        });
    }

    public void setDraggable(boolean draggable) {
        this.draggable = draggable;
        if (draggable)
            wrapper.addStyleName(draggableClass);
        else
            wrapper.removeStyleName(draggableClass);
    }

    public boolean isDraggable() {
        return draggable;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
        if (disabled)
            wrapper.addStyleName(disabledClass);
        else
            wrapper.removeStyleName(disabledClass);
    }

    public boolean isEnabled() {
        return !disabled;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public Card getCard() {
        return card;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CardWidget that = (CardWidget) o;
        return card == that.card;
    }

    @Override
    public int hashCode() {
        return card.hashCode();
    }

    @Override public String toString() {
        return "CardWidget{" +
            "draggable=" + draggable +
            ", disabled=" + disabled +
            ", card=" + card +
            '}';
    }
}
