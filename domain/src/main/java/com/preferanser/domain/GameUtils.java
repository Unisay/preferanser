package com.preferanser.domain;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;

import java.util.Collection;
import java.util.Map;

import static com.preferanser.domain.TableLocation.*;

public abstract class GameUtils {

    private GameUtils() {
    }

    static Cardinal tableLocationToCardinal(TableLocation location) {
        switch (location) {
            case NORTH:
                return Cardinal.NORTH;
            case EAST:
                return Cardinal.EAST;
            case SOUTH:
                return Cardinal.SOUTH;
            case WEST:
                return Cardinal.WEST;
            default:
                throw new IllegalArgumentException("TableLocation " + location + " doesn't have a corresponding Cardinal!");
        }
    }

    static Map<TableLocation, Collection<Card>> copyDefensive(Multimap<Cardinal, Card> cardinalCardMultimap) {
        ImmutableMap.Builder<TableLocation, Collection<Card>> builder = ImmutableMap.builder();
        if (cardinalCardMultimap.containsKey(Cardinal.NORTH)) {
            builder.put(NORTH, ImmutableList.copyOf(cardinalCardMultimap.get(Cardinal.NORTH)));
        }
        if (cardinalCardMultimap.containsKey(Cardinal.EAST)) {
            builder.put(EAST, ImmutableList.copyOf(cardinalCardMultimap.get(Cardinal.EAST)));
        }
        if (cardinalCardMultimap.containsKey(Cardinal.SOUTH)) {
            builder.put(SOUTH, ImmutableList.copyOf(cardinalCardMultimap.get(Cardinal.SOUTH)));
        }
        if (cardinalCardMultimap.containsKey(Cardinal.WEST)) {
            builder.put(WEST, ImmutableList.copyOf(cardinalCardMultimap.get(Cardinal.WEST)));
        }
        return builder.build();
    }

}
