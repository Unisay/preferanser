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


import com.google.common.collect.ImmutableList;

public enum Hand {

    EAST, SOUTH, WEST, NORTH;

    public static ImmutableList<Hand> PLAYING_HANDS = ImmutableList.of(EAST, SOUTH, WEST);

    public static Hand valueOf(TableLocation tableLocation) {
        switch (tableLocation) {
            case EAST:
                return Hand.EAST;
            case SOUTH:
                return Hand.SOUTH;
            case WEST:
                return Hand.WEST;
            case WIDOW:
                return Hand.NORTH;
            default:
                throw new IllegalArgumentException("TableLocation " + tableLocation + " doesn't have a corresponding " +
                        "Hand!");
        }
    }

}
