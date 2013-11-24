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

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.preferanser.domain.exception.DuplicateGameTurnException;
import com.preferanser.domain.exception.NoSuchCardinalCardException;
import com.preferanser.domain.exception.NoTurnsAllowedException;
import com.preferanser.domain.exception.NotInTurnException;
import com.preferanser.util.EnumRotator;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Map;

import static com.google.common.collect.Maps.newLinkedHashMap;
import static com.preferanser.domain.Card.*;
import static com.preferanser.domain.Cardinal.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

/**
 * Unit test for the Game
 */
public class GameTest {

    private Game game;
    private Map<Card, Cardinal> centerCardCardinalMap;
    private Multimap<Cardinal, Card> cardinalCardMultimap;

    @BeforeMethod
    public void setUp() throws Exception {
        cardinalCardMultimap = LinkedHashMultimap.create();
        cardinalCardMultimap.put(NORTH, CLUB_ACE);
        cardinalCardMultimap.put(NORTH, CLUB_ACE);
        cardinalCardMultimap.put(EAST, CLUB_EIGHT);
        cardinalCardMultimap.put(EAST, SPADE_EIGHT);
        cardinalCardMultimap.put(SOUTH, CLUB_KING);
        cardinalCardMultimap.put(WEST, CLUB_JACK);
        cardinalCardMultimap.put(WEST, CLUB_NINE);

        centerCardCardinalMap = newLinkedHashMap();
        game = new Game(
            4,
            createCardinalContractMap(),
            createTurnRotator(EAST, SOUTH),
            cardinalCardMultimap,
            centerCardCardinalMap
        );
    }

    @Test
    public void testMakeTurn_FromCardinalWrongCard() throws Exception {
        try {
            game.makeTurn(EAST, DIAMOND_ACE);
            fail("NoSuchCardinalCardException must have been thrown!");
        } catch (NoSuchCardinalCardException e) {
            assertThat(e.getCard(), equalTo(DIAMOND_ACE));
            assertThat(e.getCardinal(), equalTo(EAST));
            assertThat(e.getMessage(), equalTo("Can't make turn because there is no DIAMOND_ACE at EAST"));
        }
    }

    @Test
    public void testMakeTurn_NotInTurn() throws Exception {
        try {
            game.makeTurn(WEST, CLUB_JACK);
            fail("NotInTurnException must have been thrown!");
        } catch (NotInTurnException e) {
            assertThat(e.getFromCardinal(), equalTo(WEST));
            assertThat(e.getCurrentCardinal(), equalTo(EAST));
            assertThat(e.getMessage(), equalTo("WEST attempted to make turn while current turn does EAST"));
        }
    }

    @Test
    public void testMakeTurn_FourPlayersExtraTurn() throws Exception {
        game.makeTurn(EAST, CLUB_EIGHT);
        game.makeTurn(WEST, CLUB_JACK);
        game.makeTurn(NORTH, CLUB_ACE);
        try {
            game.makeTurn(EAST, SPADE_EIGHT);
            fail("NoTurnsAllowedException must have been thrown!");
        } catch (DuplicateGameTurnException e) {
            assertThat(e.getFromCardinal(), equalTo(EAST));
            assertThat(e.getCenterCardCardinalMap(), equalTo(game.getCenterCards()));
        }
    }

    @Test
    public void testMakeTurn_ThreePlayersExtraTurn() throws Exception {
        game = new Game(3, createCardinalContractMap(), createTurnRotator(NORTH), cardinalCardMultimap, centerCardCardinalMap);

        game.makeTurn(NORTH, CLUB_ACE);
        game.makeTurn(EAST, CLUB_EIGHT);
        game.makeTurn(SOUTH, CLUB_KING);

        try {
            game.makeTurn(WEST, CLUB_JACK);
            fail("NoTurnsAllowedException must have been thrown!");
        } catch (NoTurnsAllowedException e) {
            assertThat(e.getCenterCardCardinalMap(), equalTo(game.getCenterCards()));
        }
    }

    @Test
    public void testMakeTurn_SkipWidow() throws Exception {
        game = new Game(
            4,
            createCardinalContractMap(),
            createTurnRotator(NORTH, SOUTH),
            cardinalCardMultimap,
            centerCardCardinalMap
        );

        game.makeTurn(NORTH, CLUB_ACE);
        game.makeTurn(EAST, CLUB_EIGHT);
        assertEquals(game.getTurn(), WEST, "Next turn is expected to be WEST, as SOUTH is skipped (widow)");
    }


    private EnumRotator<Cardinal> createTurnRotator(Cardinal curValue, Cardinal... valuesToSkip) {
        EnumRotator<Cardinal> turnRotator = new EnumRotator<Cardinal>(Cardinal.values(), curValue);
        turnRotator.setSkipValues(valuesToSkip);
        return turnRotator;
    }

    private Map<Cardinal, Contract> createCardinalContractMap() {
        return Maps.newHashMap();
    }

}