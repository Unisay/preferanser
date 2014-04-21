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

package com.preferanser.shared.domain;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.googlecode.objectify.annotation.Embed;
import com.preferanser.shared.domain.exception.EmptyWidowException;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

@Embed
public class Widow implements Serializable, Iterable<Card> {

    public Card card1;
    public Card card2;

    public Widow() {
    }

    public Widow(Card card1, Card card2) {
        this.card1 = card1;
        this.card2 = card2;
    }

    public Widow(Widow widow) {
        this(widow.card1, widow.card2);
    }

    public Card getFirstCard() throws EmptyWidowException {
        Card result;
        if (card1 != null) {
            result = card1;
        } else if (card2 != null) {
            result = card2;
        } else {
            throw new EmptyWidowException();
        }
        return result;
    }

    public Card getCard1() {
        return card1;
    }

    public void setCard1(Card card1) {
        this.card1 = card1;
    }

    public Card getCard2() {
        return card2;
    }

    public void setCard2(Card card2) {
        this.card2 = card2;
    }

    public static Widow fromArray(Card[] cards) {
        Preconditions.checkArgument(cards.length == 2, "Expected array of exactly 2 cards");
        Preconditions.checkNotNull(cards[0], "Card 1 is null, can't create Widow");
        Preconditions.checkNotNull(cards[1], "Card 2 is null, can't create Widow");
        return new Widow(cards[0], cards[1]);
    }

    public List<Card> asList() {
        List<Card> cards = newArrayList();
        if (card1 != null)
            cards.add(card1);
        if (card2 != null)
            cards.add(card2);
        return cards;
    }

    @Override
    public Iterator<Card> iterator() {
        return asList().iterator();
    }

    public boolean hasTwoCards() {
        return card1 != null && card2 != null;
    }

    public boolean remove(Card card) {
        if (card1 == card) {
            card1 = null;
            return true;
        } else if (card2 == card) {
            card2 = null;
            return true;
        } else return false;
    }

    public boolean add(Card card) {
        if (card1 == null) {
            card1 = card;
            return true;
        } else if (card2 == null) {
            card2 = card;
            return true;
        } else return false;
    }

    public boolean containsCard(Card card) {
        return card1 == card || card2 == card;
    }

    @Override public String toString() {
        return Objects.toStringHelper(this)
            .add("card1", card1)
            .add("card2", card2)
            .toString();
    }

    @Override
    public boolean equals(Object o) {
        return this == o || !(o == null || getClass() != o.getClass()) && this.asList().equals(((Widow) o).asList());
    }

    @Override
    public int hashCode() {
        return asList().hashCode();
    }
}
