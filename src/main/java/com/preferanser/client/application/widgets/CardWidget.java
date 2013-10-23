package com.preferanser.client.application.widgets;

import com.google.gwt.event.dom.client.DragStartEvent;
import com.google.gwt.event.dom.client.DragStartHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.user.client.ui.Image;
import com.preferanser.shared.Card;

public class CardWidget extends Image {

    public interface Handlers {
        void onCardMouseDown(CardWidget cardWidget, MouseDownEvent event);
        void onCardDragStart(CardWidget cardWidget, DragStartEvent event);
    }

    private Card card;

    public CardWidget() {
    }

    public CardWidget(Card card) {
        this();
        this.card = card;
    }

    public void setHandlers(final Handlers handlers) {
        addMouseDownHandler(new MouseDownHandler() {
            @Override public void onMouseDown(MouseDownEvent event) {
                handlers.onCardMouseDown(CardWidget.this, event);
            }
        });
        addDragStartHandler(new DragStartHandler() {
            @Override public void onDragStart(DragStartEvent event) {
                handlers.onCardDragStart(CardWidget.this, event);
            }
        });
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
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
