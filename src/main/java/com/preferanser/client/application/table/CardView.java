package com.preferanser.client.application.table;

import com.google.gwt.user.client.ui.Image;
import com.preferanser.shared.Card;

public class CardView {

    public Card card;
    public Image image;

    public CardView(Card card, Image image) {
        this.card = card;
        this.image = image;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CardView cardView = (CardView) o;
        return card == cardView.card && image.toString().equals(cardView.image.toString());
    }

    @Override
    public int hashCode() {
        int result = card.hashCode();
        result = 31 * result + image.hashCode();
        return result;
    }

    @Override public String toString() {
        return "CardView{card=" + card + ", image=" + image + '}';
    }
}
