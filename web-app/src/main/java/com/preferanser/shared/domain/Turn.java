package com.preferanser.shared.domain;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import java.io.Serializable;

public class Turn implements Serializable {

    private Hand hand;
    private Card card;

    @SuppressWarnings("unused") // For serialization
    public Turn() {
    }

    public Turn(Hand hand, Card card) {
        Preconditions.checkNotNull(hand, "Hand is null");
        Preconditions.checkNotNull(card, "Card is null");
        this.hand = hand;
        this.card = card;
    }

    public Turn(Card card, Hand hand) {
        this(hand, card);
    }

    public Hand getHand() {
        return hand;
    }

    public void setHand(Hand hand) {
        this.hand = hand;
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public Suit getSuit() {
        return getCard().getSuit();
    }

    public Rank getRank() {
        return getCard().getRank();
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Turn that = (Turn) o;
        return Objects.equal(this.hand, that.hand) && Objects.equal(this.card, that.card);
    }

    @Override public int hashCode() {
        return Objects.hashCode(hand, card);
    }

    @Override public String toString() {
        return "Turn{hand=" + hand + ", card=" + card + '}';
    }
}
