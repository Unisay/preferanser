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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import com.preferanser.shared.domain.Card;
import com.preferanser.shared.domain.Hand;
import com.preferanser.shared.domain.TableLocation;

import java.util.Collection;
import java.util.Map;

import static com.preferanser.shared.domain.TableLocation.*;

public abstract class GameUtils {

    private GameUtils() {
    }

    public static Map<TableLocation, Collection<Card>> copyDefensive(Multimap<Hand, Card> handCardMultimap) {
        ImmutableMap.Builder<TableLocation, Collection<Card>> builder = ImmutableMap.builder();

        if (handCardMultimap.containsKey(Hand.EAST))
            builder.put(EAST, ImmutableList.copyOf(handCardMultimap.get(Hand.EAST)));
        else
            builder.put(EAST, ImmutableList.<Card>of());

        if (handCardMultimap.containsKey(Hand.SOUTH))
            builder.put(SOUTH, ImmutableList.copyOf(handCardMultimap.get(Hand.SOUTH)));
        else
            builder.put(SOUTH, ImmutableList.<Card>of());

        if (handCardMultimap.containsKey(Hand.WEST))
            builder.put(WEST, ImmutableList.copyOf(handCardMultimap.get(Hand.WEST)));
        else
            builder.put(WEST, ImmutableList.<Card>of());

        builder.put(CENTER, ImmutableList.<Card>of());

        return builder.build();
    }
}
