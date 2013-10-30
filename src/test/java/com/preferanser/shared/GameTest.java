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

import com.google.common.collect.Maps;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Unit test for the Game
 */
public class GameTest {

    private Game game;

    @Before
    public void setUp() throws Exception {
        game = new Game();
    }

    @Test
    public void testPutCards() throws Exception {
        game.putCards(Cardinal.NORTH);
        Map<TableLocation, Collection<Card>> operand = Maps.newHashMap();
        assertThat(game.getTableCards(), equalTo(operand));
    }

    @Test
    public void testClearCards() throws Exception {

        Assert.fail("testClearCards is not implemented");
    }

    @Test
    public void testMoveCard() throws Exception {

        Assert.fail("testMoveCard is not implemented");
    }

    @Test
    public void testSetCardinalContract() throws Exception {

        Assert.fail("testSetCardinalContract is not implemented");
    }

    @Test
    public void testGetTableCards() throws Exception {

        Assert.fail("testGetTableCards is not implemented");
    }

    @Test
    public void testGetCardinalTricks() throws Exception {

        Assert.fail("testGetCardinalTricks is not implemented");
    }

    @Test
    public void testGetCardinalContracts() throws Exception {

        Assert.fail("testGetCardinalContracts is not implemented");
    }

    @Test
    public void testGetPlayingContract() throws Exception {

        Assert.fail("testGetPlayingContract is not implemented");
    }

    @Test
    public void testGetTrump() throws Exception {

        Assert.fail("testGetTrump is not implemented");
    }

    @Test
    public void testMoveCenterCardsToSluff() throws Exception {

        Assert.fail("testMoveCenterCardsToSluff is not implemented");
    }
}
