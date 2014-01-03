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

/**
 * Playing Card
 */
public enum Card {

    SPADE_7(Suit.SPADE, Rank.SEVEN),
    CLUB_7(Suit.CLUB, Rank.SEVEN),
    DIAMOND_7(Suit.DIAMOND, Rank.SEVEN),
    HEART_7(Suit.HEART, Rank.SEVEN),

    SPADE_8(Suit.SPADE, Rank.EIGHT),
    CLUB_8(Suit.CLUB, Rank.EIGHT),
    DIAMOND_8(Suit.DIAMOND, Rank.EIGHT),
    HEART_8(Suit.HEART, Rank.EIGHT),

    SPADE_9(Suit.SPADE, Rank.NINE),
    CLUB_9(Suit.CLUB, Rank.NINE),
    DIAMOND_9(Suit.DIAMOND, Rank.NINE),
    HEART_9(Suit.HEART, Rank.NINE),

    SPADE_10(Suit.SPADE, Rank.TEN),
    CLUB_10(Suit.CLUB, Rank.TEN),
    DIAMOND_10(Suit.DIAMOND, Rank.TEN),
    HEART_10(Suit.HEART, Rank.TEN),

    SPADE_JACK(Suit.SPADE, Rank.JACK),
    CLUB_JACK(Suit.CLUB, Rank.JACK),
    DIAMOND_JACK(Suit.DIAMOND, Rank.JACK),
    HEART_JACK(Suit.HEART, Rank.JACK),

    SPADE_QUEEN(Suit.SPADE, Rank.QUEEN),
    CLUB_QUEEN(Suit.CLUB, Rank.QUEEN),
    DIAMOND_QUEEN(Suit.DIAMOND, Rank.QUEEN),
    HEART_QUEEN(Suit.HEART, Rank.QUEEN),

    SPADE_KING(Suit.SPADE, Rank.KING),
    CLUB_KING(Suit.CLUB, Rank.KING),
    DIAMOND_KING(Suit.DIAMOND, Rank.KING),
    HEART_KING(Suit.HEART, Rank.KING),

    SPADE_ACE(Suit.SPADE, Rank.ACE),
    CLUB_ACE(Suit.CLUB, Rank.ACE),
    DIAMOND_ACE(Suit.DIAMOND, Rank.ACE),
    HEART_ACE(Suit.HEART, Rank.ACE);

    private Suit suit;
    private Rank rank;

    private Card(Suit suit, Rank rank) {
        this.suit = suit;
        this.rank = rank;
    }

    public Suit getSuit() {
        return suit;
    }

    public Rank getRank() {
        return rank;
    }

}
