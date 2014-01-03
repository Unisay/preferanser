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

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.LinkedHashMultimap;
import com.preferanser.shared.domain.exception.*;
import com.preferanser.shared.util.EnumRotator;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.google.common.collect.Maps.newLinkedHashMap;
import static com.preferanser.shared.domain.Card.*;
import static com.preferanser.shared.domain.Cardinal.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.testng.Assert.*;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

/**
 * Unit test for the Game
 */
public class GameTest {

    private Game game;
    private Map<Cardinal, Contract> cardinalContractMap;
    private EnumRotator<Cardinal> turnRotator;
    private LinkedHashMultimap<Cardinal, Card> cardinalCardMultimap;
    private LinkedHashMap<Card, Cardinal> centerCardCardinalMap;

    @BeforeMethod
    public void setUp() throws Exception {
        cardinalContractMap = createCardinalContractMap();
        turnRotator = createTurnRotator(NORTH, SOUTH);
        cardinalCardMultimap = createCardinalCardMultimap();
        centerCardCardinalMap = createCenterCardCardinalMap();
    }

    @Test
    public void testMakeTurn_FromCardinalWrongCard() throws Exception {
        game = new Game(Players.THREE, cardinalContractMap, turnRotator, cardinalCardMultimap, centerCardCardinalMap);

        try {
            game.makeTurn(NORTH, DIAMOND_ACE);
            fail("NoSuchCardinalCardException must have been thrown!");
        } catch (NoSuchCardinalCardException e) {
            assertThat(e.getCard(), equalTo(DIAMOND_ACE));
            assertThat(e.getCardinal(), equalTo(NORTH));
            assertThat(e.getMessage(), equalTo("Can't make turn because there is no DIAMOND_ACE at NORTH"));
        }
    }

    @Test
    public void testMakeTurn_NotInTurn() throws Exception {
        game = new Game(Players.THREE, cardinalContractMap, turnRotator, cardinalCardMultimap, centerCardCardinalMap);

        try {
            game.makeTurn(WEST, CLUB_JACK);
            fail("NotInTurnException must have been thrown!");
        } catch (NotInTurnException e) {
            assertThat(e.getFromCardinal(), equalTo(WEST));
            assertThat(e.getCurrentCardinal(), equalTo(NORTH));
            assertThat(e.getMessage(), equalTo("WEST attempted to make turn while current turn does NORTH"));
        }
    }

    @Test
    public void testMakeTurn_WrongTrickSuit() throws Exception {
        cardinalCardMultimap = LinkedHashMultimap.create();
        cardinalCardMultimap.put(NORTH, CLUB_ACE);
        cardinalCardMultimap.put(EAST, CLUB_KING);
        cardinalCardMultimap.put(EAST, HEART_ACE);

        game = new Game(Players.THREE, cardinalContractMap, turnRotator, cardinalCardMultimap, centerCardCardinalMap);

        game.makeTurn(NORTH, CLUB_ACE);
        try {
            game.makeTurn(EAST, HEART_ACE);
            fail("IllegalSuitException must have been thrown!");
        } catch (IllegalSuitException e) {
            assertThat(e.getExpectedSuit(), equalTo(Suit.CLUB));
            assertThat(e.getActualSuit(), equalTo(Suit.HEART));
        }
    }

    @Test
    public void testMakeTurn_TrumpInsteadOfTrickSuit() throws Exception {
        cardinalCardMultimap = LinkedHashMultimap.create();
        cardinalCardMultimap.put(NORTH, CLUB_ACE);
        cardinalCardMultimap.put(EAST, CLUB_KING);
        cardinalCardMultimap.put(EAST, HEART_ACE);
        cardinalCardMultimap.put(EAST, SPADE_ACE);

        game = new Game(Players.THREE, cardinalContractMap, turnRotator, cardinalCardMultimap, centerCardCardinalMap);

        game.makeTurn(NORTH, CLUB_ACE);
        try {
            game.makeTurn(EAST, SPADE_ACE);
            fail("IllegalSuitException must have been thrown!");
        } catch (IllegalSuitException e) {
            assertThat(e.getExpectedSuit(), equalTo(Suit.CLUB));
            assertThat(e.getActualSuit(), equalTo(Suit.SPADE));
        }
    }

    @Test
    public void testMakeTurn_OtherSuitInsteadOfTrump() throws Exception {
        cardinalCardMultimap = LinkedHashMultimap.create();
        cardinalCardMultimap.put(NORTH, CLUB_ACE);
        cardinalCardMultimap.put(EAST, HEART_ACE);
        cardinalCardMultimap.put(EAST, SPADE_ACE);

        game = new Game(Players.THREE, cardinalContractMap, turnRotator, cardinalCardMultimap, centerCardCardinalMap);

        game.makeTurn(NORTH, CLUB_ACE);
        try {
            game.makeTurn(EAST, HEART_ACE);
            fail("IllegalSuitException must have been thrown!");
        } catch (IllegalSuitException e) {
            assertThat(e.getExpectedSuit(), equalTo(Suit.SPADE));
            assertThat(e.getActualSuit(), equalTo(Suit.HEART));
        }
    }

    @Test
    public void testMakeTurn_OtherSuit() throws Exception {
        cardinalCardMultimap = LinkedHashMultimap.create();
        cardinalCardMultimap.put(NORTH, CLUB_ACE);
        cardinalCardMultimap.put(EAST, HEART_ACE);
        cardinalCardMultimap.put(EAST, DIAMOND_ACE);

        game = new Game(Players.THREE, cardinalContractMap, turnRotator, cardinalCardMultimap, centerCardCardinalMap);

        game.makeTurn(NORTH, CLUB_ACE);
        game.makeTurn(EAST, HEART_ACE);
    }

    @Test
    public void testMakeTurn_OtherSuitWhenNoTrumpGame() throws Exception {
        cardinalContractMap = ImmutableMap.of(
            NORTH, Contract.SIX_NO_TRUMP,
            EAST, Contract.PASS,
            WEST, Contract.WHIST
        );

        cardinalCardMultimap = LinkedHashMultimap.create();
        cardinalCardMultimap.put(NORTH, CLUB_ACE);
        cardinalCardMultimap.put(EAST, HEART_ACE);
        cardinalCardMultimap.put(EAST, SPADE_ACE);

        game = new Game(Players.THREE, cardinalContractMap, turnRotator, cardinalCardMultimap, centerCardCardinalMap);

        game.makeTurn(NORTH, CLUB_ACE);
        game.makeTurn(EAST, HEART_ACE);
    }

    @Test
    public void testMakeTurn_FourPlayersExtraTurn() throws Exception {
        game = new Game(Players.THREE, cardinalContractMap, turnRotator, cardinalCardMultimap, centerCardCardinalMap);

        game.makeTurn(NORTH, CLUB_ACE);
        game.makeTurn(EAST, CLUB_8);
        game.makeTurn(WEST, CLUB_JACK);
        try {
            game.makeTurn(NORTH, CLUB_KING);
            fail("NoTurnsAllowedException must have been thrown!");
        } catch (DuplicateGameTurnException e) {
            assertThat(e.getFromCardinal(), equalTo(NORTH));
            assertThat(e.getCenterCardCardinalMap(), equalTo(game.getCenterCards()));
        }
    }

    @Test
    public void testMakeTurn_ThreePlayersExtraTurn() throws Exception {
        turnRotator = createTurnRotator(NORTH);
        game = new Game(Players.THREE, cardinalContractMap, turnRotator, cardinalCardMultimap, centerCardCardinalMap);

        game.makeTurn(NORTH, CLUB_ACE);
        game.makeTurn(EAST, CLUB_8);
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
        game = new Game(Players.FOUR, cardinalContractMap, turnRotator, cardinalCardMultimap, centerCardCardinalMap);

        game.makeTurn(NORTH, CLUB_ACE);
        game.makeTurn(EAST, CLUB_8);
        assertEquals(game.getTurn(), WEST, "Next turn is expected to be WEST, as SOUTH is skipped (widow)");
    }

    @Test
    public void testIsTrickComplete_ThreePlayers_Positive() throws Exception {
        centerCardCardinalMap.clear();
        centerCardCardinalMap.put(DIAMOND_ACE, NORTH);
        centerCardCardinalMap.put(DIAMOND_KING, EAST);
        centerCardCardinalMap.put(DIAMOND_QUEEN, WEST);

        game = new Game(Players.THREE, cardinalContractMap, turnRotator, cardinalCardMultimap, centerCardCardinalMap);

        assertTrue(game.isTrickComplete());
    }

    @Test
    public void testIsTrickComplete_ThreePlayers_Negative() throws Exception {
        game = new Game(Players.THREE, cardinalContractMap, turnRotator, cardinalCardMultimap, centerCardCardinalMap);

        assertFalse(game.isTrickComplete());
    }

    @Test
    public void testIsTrickComplete_FourPlayers_Positive() throws Exception {
        centerCardCardinalMap.clear();
        centerCardCardinalMap.put(DIAMOND_ACE, NORTH);
        centerCardCardinalMap.put(DIAMOND_KING, EAST);
        centerCardCardinalMap.put(DIAMOND_JACK, SOUTH);
        centerCardCardinalMap.put(DIAMOND_QUEEN, WEST);

        turnRotator = createTurnRotator(NORTH);
        game = new Game(Players.FOUR, cardinalContractMap, turnRotator, cardinalCardMultimap, centerCardCardinalMap);

        assertTrue(game.isTrickComplete());
    }

    @Test
    public void testIsTrickComplete_FourPlayers_Negative() throws Exception {
        centerCardCardinalMap.clear();
        centerCardCardinalMap.put(DIAMOND_ACE, NORTH);
        centerCardCardinalMap.put(DIAMOND_KING, EAST);
        centerCardCardinalMap.put(DIAMOND_QUEEN, WEST);

        game = new Game(Players.FOUR, cardinalContractMap, turnRotator, cardinalCardMultimap, centerCardCardinalMap);

        assertFalse(game.isTrickComplete());
    }

    @Test
    public void testGetTurn() throws Exception {
        game = new Game(Players.THREE, cardinalContractMap, turnRotator, cardinalCardMultimap, centerCardCardinalMap);
        assertThat(game.getTurn(), equalTo(NORTH));
        game.makeTurn(NORTH, CLUB_ACE);
        assertThat(game.getTurn(), equalTo(EAST));
        game.makeTurn(EAST, CLUB_8);
        assertThat(game.getTurn(), equalTo(WEST));
        game.makeTurn(WEST, CLUB_JACK);
        assertThat(game.getTurn(), equalTo(NORTH));
    }

    @Test
    public void testGetTrump() throws Exception {
        game = new Game(Players.THREE, cardinalContractMap, turnRotator, cardinalCardMultimap, centerCardCardinalMap);

        assertTrue(game.getTrump().isPresent());
        assertThat(game.getTrump().get(), equalTo(Suit.SPADE));
    }

    @Test
    public void testGetTrump_NoTrumpPlayingContract() throws Exception {
        cardinalContractMap = ImmutableMap.of(NORTH, Contract.SIX_NO_TRUMP, EAST, Contract.PASS, WEST, Contract.WHIST);
        game = new Game(Players.THREE, cardinalContractMap, turnRotator, cardinalCardMultimap, centerCardCardinalMap);

        assertFalse(game.getTrump().isPresent());
    }

    @Test
    public void testGetTrump_NoTrumpNotPlayingContract() throws Exception {
        cardinalContractMap = ImmutableMap.of(NORTH, Contract.PASS, EAST, Contract.PASS, WEST, Contract.PASS);
        game = new Game(Players.THREE, cardinalContractMap, turnRotator, cardinalCardMultimap, centerCardCardinalMap);

        assertFalse(game.getTrump().isPresent());
    }

    @Test
    public void testSluffTrick() throws Exception {
        game = new Game(Players.THREE, cardinalContractMap, turnRotator, cardinalCardMultimap, centerCardCardinalMap);
        assertFalse(game.sluffTrick());
        game.makeTurn(NORTH, CLUB_ACE);
        assertFalse(game.sluffTrick());
        game.makeTurn(EAST, CLUB_8);
        assertFalse(game.sluffTrick());
        game.makeTurn(WEST, CLUB_JACK);
        assertTrue(game.sluffTrick());

        assertTrue(game.getCenterCards().isEmpty());
        assertThat(turnRotator.current(), equalTo(NORTH));
        assertReflectionEquals(ImmutableMap.of(NORTH, 1, EAST, 0, WEST, 0, SOUTH, 0), game.getCardinalTricks());
    }

    private EnumRotator<Cardinal> createTurnRotator(Cardinal curValue, Cardinal... valuesToSkip) {
        EnumRotator<Cardinal> turnRotator = new EnumRotator<Cardinal>(Cardinal.values(), curValue);
        turnRotator.setSkipValues(valuesToSkip);
        return turnRotator;
    }

    private Map<Cardinal, Contract> createCardinalContractMap() {
        return ImmutableMap.of(
            NORTH, Contract.SIX_SPADE,
            EAST, Contract.PASS,
            WEST, Contract.WHIST
        );
    }

    private LinkedHashMap<Card, Cardinal> createCenterCardCardinalMap() {
        return newLinkedHashMap();
    }

    private LinkedHashMultimap<Cardinal, Card> createCardinalCardMultimap() {
        LinkedHashMultimap<Cardinal, Card> multimap = LinkedHashMultimap.create();
        multimap.put(NORTH, CLUB_ACE);
        multimap.put(NORTH, CLUB_KING);
        multimap.put(EAST, CLUB_8);
        multimap.put(EAST, SPADE_8);
        multimap.put(SOUTH, CLUB_KING);
        multimap.put(WEST, CLUB_JACK);
        multimap.put(WEST, CLUB_9);
        multimap.put(WEST, HEART_JACK);
        return multimap;
    }

}