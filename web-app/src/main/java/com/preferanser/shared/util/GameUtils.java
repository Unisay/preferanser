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

package com.preferanser.shared.util;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.preferanser.shared.domain.Card;
import com.preferanser.shared.domain.Hand;

import java.util.Map;
import java.util.Set;


public abstract class GameUtils {

    private GameUtils() {
    }

    public static Map<Hand, Set<Card>> copyDefensive(Multimap<Hand, Card> handCardMultimap) {
        ImmutableMap.Builder<Hand, Set<Card>> builder = ImmutableMap.builder();

        if (handCardMultimap.containsKey(Hand.EAST))
            builder.put(Hand.EAST, ImmutableSet.copyOf(handCardMultimap.get(Hand.EAST)));
        else
            builder.put(Hand.EAST, ImmutableSet.<Card>of());

        if (handCardMultimap.containsKey(Hand.SOUTH))
            builder.put(Hand.SOUTH, ImmutableSet.copyOf(handCardMultimap.get(Hand.SOUTH)));
        else
            builder.put(Hand.SOUTH, ImmutableSet.<Card>of());

        if (handCardMultimap.containsKey(Hand.WEST))
            builder.put(Hand.WEST, ImmutableSet.copyOf(handCardMultimap.get(Hand.WEST)));
        else
            builder.put(Hand.WEST, ImmutableSet.<Card>of());

        return builder.build();
    }
}
