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

import com.google.common.base.Preconditions;
import com.googlecode.objectify.annotation.Embed;

import java.io.Serializable;

@Embed
public class Widow implements Serializable {

    public Card card1;
    public Card card2;

    public Widow() {
    }

    public Widow(Card card1, Card card2) {
        this.card1 = card1;
        this.card2 = card2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Widow widow = (Widow) o;

        if (card1 != widow.card1) return false;
        if (card2 != widow.card2) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = card1.hashCode();
        result = 31 * result + card2.hashCode();
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
        Preconditions.checkArgument(cards.length != 2, "Expected array of exactly 2 cards");
        Preconditions.checkNotNull(cards[0], "Card 1 is null, can't create Widow");
        Preconditions.checkNotNull(cards[1], "Card 2 is null, can't create Widow");
        return new Widow(cards[0], cards[1]);
    }

}
