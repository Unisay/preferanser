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
import com.preferanser.shared.domain.exception.DuplicateGameTurnException;
import com.preferanser.shared.domain.exception.IllegalSuitException;
import com.preferanser.shared.domain.exception.NoSuchHandCardException;
import com.preferanser.shared.domain.exception.NotInTurnException;
import com.preferanser.shared.util.EnumRotator;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.google.appengine.labs.repackaged.com.google.common.collect.Sets.newHashSet;
import static com.google.common.collect.Maps.newLinkedHashMap;
import static com.preferanser.shared.domain.Card.*;
import static com.preferanser.shared.domain.Hand.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.testng.Assert.*;
import static org.unitils.reflectionassert.ReflectionAssert.assertLenientEquals;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

/**
 * Unit test for the Game
 */
public class GameTest {

    private Game game;
    private Widow widow;
    private Map<Hand, Contract> handContractMap;
    private EnumRotator<Hand> turnRotator;
    private LinkedHashMultimap<Hand, Card> handCardMultimap;
    private LinkedHashMap<Card, Hand> centerCardHandMap;

    @BeforeMethod
    public void setUp() throws Exception {
        widow = new Widow();
        handContractMap = createHandContractMap();
        turnRotator = createTurnRotator(SOUTH, NORTH);
        handCardMultimap = createHandCardMultimap();
        centerCardHandMap = createCenterCardHandMap();
    }

    @Test
    public void testMakeTurn_FromHandWrongCard() throws Exception {
        game = new Game(Players.THREE, widow, handContractMap, turnRotator, handCardMultimap, centerCardHandMap);

        try {
            game.makeTurn(SOUTH, DIAMOND_ACE);
            fail("NoSuchHandCardException must have been thrown!");
        } catch (NoSuchHandCardException e) {
            assertThat(e.getCard(), equalTo(DIAMOND_ACE));
            assertThat(e.getHand(), equalTo(SOUTH));
            assertThat(e.getMessage(), equalTo("Can't make turn because there is no DIAMOND_ACE at SOUTH"));
        }
    }

    @Test
    public void testMakeTurn_NotInTurn() throws Exception {
        game = new Game(Players.THREE, widow, handContractMap, turnRotator, handCardMultimap, centerCardHandMap);

        try {
            game.makeTurn(WEST, CLUB_JACK);
            fail("NotInTurnException must have been thrown!");
        } catch (NotInTurnException e) {
            assertThat(e.getFromHand(), equalTo(WEST));
            assertThat(e.getCurrentHand(), equalTo(SOUTH));
            assertThat(e.getMessage(), equalTo("WEST attempted to make turn while current turn does SOUTH"));
        }
    }

    @Test
    public void testMakeTurn_WrongTrickSuit() throws Exception {
        handCardMultimap.clear();
        handCardMultimap.put(SOUTH, CLUB_ACE);
        handCardMultimap.put(WEST, HEART_ACE);
        handCardMultimap.put(WEST, CLUB_KING);

        game = new Game(Players.THREE, widow, handContractMap, turnRotator, handCardMultimap, centerCardHandMap);

        game.makeTurn(SOUTH, CLUB_ACE);
        try {
            game.makeTurn(WEST, HEART_ACE);
            fail("IllegalSuitException must have been thrown!");
        } catch (IllegalSuitException e) {
            assertThat(e.getActualSuit(), equalTo(Suit.HEART));
            assertThat(e.getExpectedSuit(), equalTo(Suit.CLUB));
        }
    }

    @Test
    public void testMakeTurn_TrumpInsteadOfTrickSuit() throws Exception {
        handCardMultimap.clear();
        handCardMultimap.put(SOUTH, CLUB_ACE);
        handCardMultimap.put(WEST, CLUB_KING);
        handCardMultimap.put(WEST, HEART_ACE);
        handCardMultimap.put(WEST, SPADE_ACE);

        game = new Game(Players.THREE, widow, handContractMap, turnRotator, handCardMultimap, centerCardHandMap);

        game.makeTurn(SOUTH, CLUB_ACE);
        try {
            game.makeTurn(WEST, SPADE_ACE);
            fail("IllegalSuitException must have been thrown!");
        } catch (IllegalSuitException e) {
            assertThat(e.getActualSuit(), equalTo(Suit.SPADE));
            assertThat(e.getExpectedSuit(), equalTo(Suit.CLUB));
        }
    }

    @Test
    public void testMakeTurn_OtherSuitInsteadOfTrump() throws Exception {
        handCardMultimap = LinkedHashMultimap.create();
        handCardMultimap.put(SOUTH, CLUB_ACE);
        handCardMultimap.put(WEST, HEART_ACE);
        handCardMultimap.put(WEST, SPADE_ACE);

        game = new Game(Players.THREE, widow, handContractMap, turnRotator, handCardMultimap, centerCardHandMap);

        game.makeTurn(SOUTH, CLUB_ACE);
        try {
            game.makeTurn(WEST, HEART_ACE);
            fail("IllegalSuitException must have been thrown!");
        } catch (IllegalSuitException e) {
            assertThat(e.getActualSuit(), equalTo(Suit.HEART));
            assertThat(e.getExpectedSuit(), equalTo(Suit.SPADE));
        }
    }

    @Test
    public void testMakeTurn_OtherSuit() throws Exception {
        handCardMultimap = LinkedHashMultimap.create();
        handCardMultimap.put(SOUTH, CLUB_ACE);
        handCardMultimap.put(WEST, HEART_ACE);
        handCardMultimap.put(WEST, DIAMOND_ACE);

        game = new Game(Players.THREE, widow, handContractMap, turnRotator, handCardMultimap, centerCardHandMap);

        game.makeTurn(SOUTH, CLUB_ACE);
        game.makeTurn(WEST, HEART_ACE);
    }

    @Test
    public void testMakeTurn_OtherSuitWhenNoTrumpGame() throws Exception {
        handContractMap = ImmutableMap.of(
            EAST, Contract.PASS,
            SOUTH, Contract.SIX_NO_TRUMP,
            WEST, Contract.WHIST
        );

        handCardMultimap = LinkedHashMultimap.create();
        handCardMultimap.put(SOUTH, CLUB_ACE);
        handCardMultimap.put(WEST, HEART_ACE);
        handCardMultimap.put(WEST, SPADE_ACE);

        game = new Game(Players.THREE, widow, handContractMap, turnRotator, handCardMultimap, centerCardHandMap);

        game.makeTurn(SOUTH, CLUB_ACE);
        game.makeTurn(WEST, HEART_ACE);
    }

    @Test
    public void testMakeTurn_DuplicateGameTurn() throws Exception {
        game = new Game(Players.THREE, widow, handContractMap, turnRotator, handCardMultimap, centerCardHandMap);

        game.makeTurn(SOUTH, CLUB_ACE);
        game.makeTurn(WEST, CLUB_9);
        game.makeTurn(EAST, CLUB_8);
        try {
            game.makeTurn(SOUTH, CLUB_KING);
            fail("DuplicateGameTurnException must have been thrown!");
        } catch (DuplicateGameTurnException e) {
            assertThat(e.getFromHand(), equalTo(SOUTH));
            assertThat(e.getCenterCardHandMap(), equalTo(game.getCenterCards()));
        }
    }

    @Test
    public void testIsTrickComplete_ThreePlayers_Positive() throws Exception {
        centerCardHandMap.clear();
        centerCardHandMap.put(DIAMOND_ACE, SOUTH);
        centerCardHandMap.put(DIAMOND_QUEEN, WEST);
        centerCardHandMap.put(DIAMOND_KING, EAST);

        game = new Game(Players.THREE, widow, handContractMap, turnRotator, handCardMultimap, centerCardHandMap);

        assertTrue(game.isTrickComplete());
    }

    @Test
    public void testIsTrickComplete_ThreePlayers_Negative() throws Exception {
        game = new Game(Players.THREE, widow, handContractMap, turnRotator, handCardMultimap, centerCardHandMap);

        assertFalse(game.isTrickComplete());
    }

    @Test
    public void testIsTrickComplete_FourPlayers_Positive() throws Exception {
        centerCardHandMap.clear();
        centerCardHandMap.put(DIAMOND_ACE, NORTH);
        centerCardHandMap.put(DIAMOND_KING, EAST);
        centerCardHandMap.put(DIAMOND_JACK, SOUTH);
        centerCardHandMap.put(DIAMOND_QUEEN, WEST);

        turnRotator = createTurnRotator(SOUTH, NORTH);
        game = new Game(Players.FOUR, widow, handContractMap, turnRotator, handCardMultimap, centerCardHandMap);

        assertTrue(game.isTrickComplete());
    }

    @Test
    public void testIsTrickComplete_FourPlayers_Negative() throws Exception {
        centerCardHandMap.clear();
        centerCardHandMap.put(DIAMOND_KING, EAST);
        centerCardHandMap.put(DIAMOND_ACE, SOUTH);
        centerCardHandMap.put(DIAMOND_QUEEN, WEST);

        game = new Game(Players.FOUR, widow, handContractMap, turnRotator, handCardMultimap, centerCardHandMap);

        assertFalse(game.isTrickComplete());
    }

    @Test
    public void testGetTurn() throws Exception {
        game = new Game(Players.THREE, widow, handContractMap, turnRotator, handCardMultimap, centerCardHandMap);
        assertThat(game.getTurn(), equalTo(SOUTH));
        game.makeTurn(SOUTH, CLUB_ACE);
        assertThat(game.getTurn(), equalTo(WEST));
        game.makeTurn(WEST, CLUB_9);
        assertThat(game.getTurn(), equalTo(EAST));
        game.makeTurn(EAST, CLUB_8);
        assertThat(game.getTurn(), equalTo(SOUTH));
    }

    @Test
    public void testGetTrump() throws Exception {
        game = new Game(Players.THREE, widow, handContractMap, turnRotator, handCardMultimap, centerCardHandMap);

        assertTrue(game.getTrump().isPresent());
        assertThat(game.getTrump().get(), equalTo(Suit.SPADE));
    }

    @Test
    public void testGetTrump_NoTrumpPlayingContract() throws Exception {
        handContractMap = ImmutableMap.of(SOUTH, Contract.SIX_NO_TRUMP, EAST, Contract.PASS, WEST, Contract.WHIST);
        game = new Game(Players.THREE, widow, handContractMap, turnRotator, handCardMultimap, centerCardHandMap);

        assertFalse(game.getTrump().isPresent());
    }

    @Test
    public void testGetTrump_NoTrumpNotPlayingContract() throws Exception {
        handContractMap = ImmutableMap.of(SOUTH, Contract.PASS, EAST, Contract.PASS, WEST, Contract.PASS);
        game = new Game(Players.THREE, widow, handContractMap, turnRotator, handCardMultimap, centerCardHandMap);

        assertFalse(game.getTrump().isPresent());
    }

    @Test
    public void testSluffTrick() throws Exception {
        game = new Game(Players.THREE, widow, handContractMap, turnRotator, handCardMultimap, centerCardHandMap);
        assertFalse(game.sluffTrick());
        game.makeTurn(SOUTH, CLUB_ACE);
        assertFalse(game.sluffTrick());
        game.makeTurn(WEST, CLUB_JACK);
        assertFalse(game.sluffTrick());
        game.makeTurn(EAST, CLUB_8);
        assertTrue(game.sluffTrick());

        assertTrue(game.getCenterCards().isEmpty());
        assertThat(turnRotator.current(), equalTo(SOUTH));
        assertReflectionEquals(ImmutableMap.of(SOUTH, 1, EAST, 0, WEST, 0, NORTH, 0), game.getHandTricks());
    }

    @Test
    public void testGetDisabledCards() throws Exception {
        game = new Game(Players.THREE, widow, handContractMap, turnRotator, handCardMultimap, centerCardHandMap);
        assertLenientEquals(newHashSet(CLUB_8, SPADE_8, CLUB_JACK, CLUB_9, HEART_JACK), game.getDisabledCards());
        game.makeTurn(SOUTH, CLUB_ACE);
        assertLenientEquals(newHashSet(CLUB_8, SPADE_8, CLUB_KING, HEART_JACK), game.getDisabledCards());
        game.makeTurn(WEST, CLUB_JACK);
        assertLenientEquals(newHashSet(SPADE_8, CLUB_KING, HEART_JACK, CLUB_9), game.getDisabledCards());
        game.makeTurn(EAST, CLUB_8);
        assertLenientEquals(newHashSet(SPADE_8, CLUB_KING, CLUB_9, HEART_JACK), game.getDisabledCards());
    }

    private EnumRotator<Hand> createTurnRotator(Hand curValue, Hand... valuesToSkip) {
        EnumRotator<Hand> turnRotator = new EnumRotator<Hand>(Hand.values(), curValue);
        turnRotator.setSkipValues(valuesToSkip);
        return turnRotator;
    }

    private Map<Hand, Contract> createHandContractMap() {
        return ImmutableMap.of(
            EAST, Contract.PASS,
            SOUTH, Contract.SIX_SPADE,
            WEST, Contract.WHIST
        );
    }

    private LinkedHashMap<Card, Hand> createCenterCardHandMap() {
        return newLinkedHashMap();
    }

    private LinkedHashMultimap<Hand, Card> createHandCardMultimap() {
        LinkedHashMultimap<Hand, Card> multimap = LinkedHashMultimap.create();
        multimap.put(EAST, CLUB_8);
        multimap.put(EAST, SPADE_8);
        multimap.put(SOUTH, CLUB_ACE);
        multimap.put(SOUTH, CLUB_KING);
        multimap.put(WEST, CLUB_JACK);
        multimap.put(WEST, CLUB_9);
        multimap.put(WEST, HEART_JACK);
        return multimap;
    }

}