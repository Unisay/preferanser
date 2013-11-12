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

import org.apache.commons.lang.ArrayUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.preferanser.domain.Cardinal.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

public class GameBuilderTest {

    private Game.Builder builder;
    private Card[] northCards;
    private Card[] eastCards;
    private Card[] westCards;

    @Before
    public void setUp() throws Exception {
        northCards = (Card[]) ArrayUtils.subarray(Card.values(), 0, 10);
        eastCards = (Card[]) ArrayUtils.subarray(Card.values(), 10, 20);
        westCards = (Card[]) ArrayUtils.subarray(Card.values(), 20, 30);
        builder = new Game.Builder()
                .threePlayerGame()
                .cardinalContract(NORTH, Contract.SIX_SPADE)
                .cardinalContract(EAST, Contract.WHIST)
                .cardinalContract(WEST, Contract.PASS)
                .putCards(NORTH, northCards)
                .putCards(EAST, eastCards)
                .putCards(WEST, westCards)
                .firstTurn(NORTH);
    }

    @Test
    public void testBuild_ThreePlayerGame() throws Exception {
        Game game = builder.threePlayerGame().build();
        assertThat(game.getNumPlayers(), equalTo(3));
    }

    @Test
    public void testBuild_FourPlayerGame() throws Exception {
        Game game = builder.fourPlayerGame().build();
        assertThat(game.getNumPlayers(), equalTo(4));
    }

    @Test
    public void testFirstTurn() throws Exception {
        assertThat(builder.firstTurn(EAST).build().getTurn(), equalTo(EAST));
    }

    @Test(expected = GameBuilderException.class)
    public void testFirstTurn_Wrong() throws Exception {
        builder.firstTurn(SOUTH).build();
    }

    @Test
    public void testCardinalContract() throws Exception {
        Map<Cardinal, Contract> expectedContracts = newHashMap();
        expectedContracts.put(NORTH, Contract.SIX_SPADE);
        expectedContracts.put(EAST, Contract.WHIST);
        expectedContracts.put(WEST, Contract.PASS);

        Map<Cardinal, Contract> actualContracts = builder.build().getCardinalContracts();
        assertReflectionEquals(expectedContracts, actualContracts);
        assertThat(actualContracts, not(hasKey(SOUTH)));
    }

    @Test
    public void testPutCards_Array() throws Exception {
        Game game = builder.build();

        assertThat(game.getCardsByCardinal(NORTH), equalTo((Collection<Card>) newArrayList(northCards)));
        assertThat(game.getCardsByCardinal(EAST), equalTo((Collection<Card>) newArrayList(eastCards)));
        assertThat(game.getCardsByCardinal(WEST), equalTo((Collection<Card>) newArrayList(westCards)));
        assertThat(game.getCardsByCardinal(SOUTH), empty());
    }
}