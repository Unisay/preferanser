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

package com.preferanser.domain;

import com.google.common.base.Optional;

import static com.preferanser.domain.Suit.*;

public enum Contract {

    SIX_SPADE(6, SPADE),
    SIX_CLUB(6, CLUB),
    SIX_DIAMOND(6, DIAMOND),
    SIX_HEART(6, HEART),
    SIX_NO_TRUMP(6, null),

    SEVEN_SPADE(7, SPADE),
    SEVEN_CLUB(7, CLUB),
    SEVEN_DIAMOND(7, DIAMOND),
    SEVEN_HEART(7, HEART),
    SEVEN_NO_TRUMP(7, null),

    EIGHT_SPADE(8, SPADE),
    EIGHT_CLUB(8, CLUB),
    EIGHT_DIAMOND(8, DIAMOND),
    EIGHT_HEART(8, HEART),
    EIGHT_NO_TRUMP(8, null),

    NINE_SPADE(9, SPADE),
    NINE_CLUB(9, CLUB),
    NINE_DIAMOND(9, DIAMOND),
    NINE_HEART(9, HEART),
    NINE_NO_TRUMP(9, null),

    TEN_SPADE(10, SPADE),
    TEN_CLUB(10, CLUB),
    TEN_DIAMOND(10, DIAMOND),
    TEN_HEART(10, HEART),
    TEN_NO_TRUMP(10, null),

    MISER(0, null),
    PASS(null, null),
    WHIST(null, null);

    private Contract(Integer tricksNumber, Suit trumpSuit) {
        this.tricksNumber = tricksNumber;
        this.trump = trumpSuit;
    }

    private Suit trump;
    private final Integer tricksNumber;

    public Integer getTricksNumber() {
        return tricksNumber;
    }

    public Optional<Suit> getTrump() {
        return Optional.fromNullable(trump);
    }

    public boolean isPlaying() {
        return tricksNumber != null;
    }

    public boolean isNotPlaying() {
        return tricksNumber == null;
    }
}
