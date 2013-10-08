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

package com.preferanser.shared;

/**
 * Playing Card
 */
public enum Card {

    CLUB_SEVEN(Suit.CLUB, Rank.SEVEN),
    SPADE_SEVEN(Suit.SPADE, Rank.SEVEN),
    DIAMOND_SEVEN(Suit.DIAMOND, Rank.SEVEN),
    HEART_SEVEN(Suit.HEART, Rank.SEVEN),

    CLUB_EIGHT(Suit.CLUB, Rank.EIGHT),
    SPADE_EIGHT(Suit.SPADE, Rank.EIGHT),
    DIAMOND_EIGHT(Suit.DIAMOND, Rank.EIGHT),
    HEART_EIGHT(Suit.HEART, Rank.EIGHT),

    CLUB_NINE(Suit.CLUB, Rank.NINE),
    SPADE_NINE(Suit.SPADE, Rank.NINE),
    DIAMOND_NINE(Suit.DIAMOND, Rank.NINE),
    HEART_NINE(Suit.HEART, Rank.NINE),

    CLUB_TEN(Suit.CLUB, Rank.TEN),
    SPADE_TEN(Suit.SPADE, Rank.TEN),
    DIAMOND_TEN(Suit.DIAMOND, Rank.TEN),
    HEART_TEN(Suit.HEART, Rank.TEN),

    CLUB_JACK(Suit.CLUB, Rank.JACK),
    SPADE_JACK(Suit.SPADE, Rank.JACK),
    DIAMOND_JACK(Suit.DIAMOND, Rank.JACK),
    HEART_JACK(Suit.HEART, Rank.JACK),

    CLUB_QUEEN(Suit.CLUB, Rank.QUEEN),
    SPADE_QUEEN(Suit.SPADE, Rank.QUEEN),
    DIAMOND_QUEEN(Suit.DIAMOND, Rank.QUEEN),
    HEART_QUEEN(Suit.HEART, Rank.QUEEN),

    CLUB_KING(Suit.CLUB, Rank.KING),
    SPADE_KING(Suit.SPADE, Rank.KING),
    DIAMOND_KING(Suit.DIAMOND, Rank.KING),
    HEART_KING(Suit.HEART, Rank.KING),

    CLUB_ACE(Suit.CLUB, Rank.ACE),
    SPADE_ACE(Suit.SPADE, Rank.ACE),
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
