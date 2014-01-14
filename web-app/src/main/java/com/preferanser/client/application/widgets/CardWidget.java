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

    private static final String CARD_DISABLED_CLASS_NAME = "card-disabled";
    private final SimplePanel wrapper;
    private final Image image;

    public interface Handlers {
        void onCardMouseDown(CardWidget cardWidget, MouseDownEvent event);
        void onCardDragStart(CardWidget cardWidget, DragStartEvent event);
        void onCardDoubleClick(CardWidget cardWidget, DoubleClickEvent event);
    }

    private Card card;

    public CardWidget(Card card) {
        this.card = card;
        image = new Image();
        wrapper = new SimplePanel(image);
        initWidget(wrapper);
    }

    public void setDisabled(boolean disabled) {
        if (disabled)
            wrapper.addStyleName(CARD_DISABLED_CLASS_NAME);
        else
            wrapper.removeStyleName(CARD_DISABLED_CLASS_NAME);
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
}
