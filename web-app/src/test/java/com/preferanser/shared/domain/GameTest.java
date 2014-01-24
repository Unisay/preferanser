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
import com.preferanser.shared.domain.entity.Deal;
import com.preferanser.shared.domain.exception.DuplicateGameTurnException;
import com.preferanser.shared.domain.exception.IllegalSuitException;
import com.preferanser.shared.domain.exception.NotInTurnException;
import com.preferanser.shared.util.EnumRotator;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.google.common.collect.Maps.newLinkedHashMap;
import static com.google.common.collect.Sets.newHashSet;
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
    private String name;
    private String description;
    private Map<Hand, Contract> handContractMap;
    private EnumRotator<Hand> turnRotator;
    private LinkedHashMultimap<Hand, Card> handCardMultimap;
    private LinkedHashMap<Card, Hand> centerCardHandMap;

    @BeforeMethod
    public void setUp() throws Exception {
        name = "name";
        description = "description";
        widow = new Widow();
        handContractMap = createHandContractMap();
        turnRotator = createTurnRotator(SOUTH, WIDOW);
        handCardMultimap = createHandCardMultimap();
        centerCardHandMap = createCenterCardHandMap();
        game = new Game(Players.THREE, widow, handContractMap, turnRotator, handCardMultimap, centerCardHandMap);
    }

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "No hand holds DIAMOND_ACE")
    public void testMakeTurn_FromHandWrongCard() throws Exception {
        game.makeTurn(DIAMOND_ACE);
    }

    @Test
    public void testMakeTurn_NotInTurn() throws Exception {
        try {
            game.makeTurn(CLUB_JACK);
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

        game.makeTurn(CLUB_ACE);
        try {
            game.makeTurn(HEART_ACE);
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

        game.makeTurn(CLUB_ACE);
        try {
            game.makeTurn(SPADE_ACE);
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

        game.makeTurn(CLUB_ACE);
        try {
            game.makeTurn(HEART_ACE);
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

        game.makeTurn(CLUB_ACE);
        game.makeTurn(HEART_ACE);
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

        game.makeTurn(CLUB_ACE);
        game.makeTurn(HEART_ACE);
    }

    @Test
    public void testMakeTurn_DuplicateGameTurn() throws Exception {
        game.makeTurn(CLUB_ACE);
        game.makeTurn(CLUB_9);
        game.makeTurn(CLUB_8);
        try {
            game.makeTurn(CLUB_KING);
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

        assertTrue(game.isTrickClosed());
    }

    @Test
    public void testIsTrickComplete_ThreePlayers_Negative() throws Exception {
        game = new Game(Players.THREE, widow, handContractMap, turnRotator, handCardMultimap, centerCardHandMap);

        assertFalse(game.isTrickClosed());
    }

    @Test
    public void testIsTrickComplete_FourPlayers_Positive() throws Exception {
        centerCardHandMap.clear();
        centerCardHandMap.put(DIAMOND_ACE, WIDOW);
        centerCardHandMap.put(DIAMOND_KING, EAST);
        centerCardHandMap.put(DIAMOND_JACK, SOUTH);
        centerCardHandMap.put(DIAMOND_QUEEN, WEST);

        turnRotator = createTurnRotator(SOUTH, WIDOW);
        game = new Game(Players.FOUR, widow, handContractMap, turnRotator, handCardMultimap, centerCardHandMap);

        assertTrue(game.isTrickClosed());
    }

    @Test
    public void testIsTrickComplete_FourPlayers_Negative() throws Exception {
        centerCardHandMap.clear();
        centerCardHandMap.put(DIAMOND_KING, EAST);
        centerCardHandMap.put(DIAMOND_ACE, SOUTH);
        centerCardHandMap.put(DIAMOND_QUEEN, WEST);

        game = new Game(Players.FOUR, widow, handContractMap, turnRotator, handCardMultimap, centerCardHandMap);

        assertFalse(game.isTrickClosed());
    }

    @Test
    public void testGetTurn() throws Exception {
        assertThat(game.getTurn(), equalTo(SOUTH));
        game.makeTurn(CLUB_ACE);
        assertThat(game.getTurn(), equalTo(WEST));
        game.makeTurn(CLUB_9);
        assertThat(game.getTurn(), equalTo(EAST));
        game.makeTurn(CLUB_8);
        assertThat(game.getTurn(), equalTo(SOUTH));
    }

    @Test
    public void testGetTrump() throws Exception {
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
        assertFalse(game.sluffTrick());
        game.makeTurn(CLUB_ACE);
        assertFalse(game.sluffTrick());
        game.makeTurn(CLUB_JACK);
        assertFalse(game.sluffTrick());
        game.makeTurn(CLUB_8);
        assertTrue(game.sluffTrick());

        assertTrue(game.getCenterCards().isEmpty());
        assertThat(game.getTurn(), equalTo(SOUTH));
        assertReflectionEquals(ImmutableMap.of(SOUTH, 1, EAST, 0, WEST, 0, WIDOW, 0), game.getHandTrickCounts());
    }

    @Test
    public void testGetDisabledCards() throws Exception {
        assertLenientEquals(newHashSet(CLUB_8, SPADE_8, CLUB_JACK, CLUB_9, HEART_JACK), game.getDisabledCards());
        game.makeTurn(CLUB_ACE);
        assertLenientEquals(newHashSet(CLUB_8, SPADE_8, CLUB_KING, HEART_JACK), game.getDisabledCards());
        game.makeTurn(CLUB_JACK);
        assertLenientEquals(newHashSet(SPADE_8, CLUB_KING, HEART_JACK, CLUB_9), game.getDisabledCards());
        game.makeTurn(CLUB_8);
        assertLenientEquals(newHashSet(SPADE_8, CLUB_KING, CLUB_9, HEART_JACK), game.getDisabledCards());
    }

    @Test
    public void testUndoRedo() throws Exception {
        handCardMultimap.clear();

        handCardMultimap.put(EAST, CLUB_8);
        handCardMultimap.put(EAST, SPADE_8);

        handCardMultimap.put(SOUTH, CLUB_7);
        handCardMultimap.put(SOUTH, CLUB_ACE);
        handCardMultimap.put(SOUTH, CLUB_KING);

        handCardMultimap.put(WEST, CLUB_JACK);
        handCardMultimap.put(WEST, CLUB_9);
        handCardMultimap.put(WEST, HEART_JACK);

        game = new Game(Players.THREE, widow, handContractMap, turnRotator, handCardMultimap, centerCardHandMap);

        game.makeTurn(CLUB_7);
        game.makeTurn(CLUB_JACK);
        game.makeTurn(CLUB_8);

        assertTrue(game.sluffTrick(), "Failed to sluff trick");
        assertThat(game.getTurn(), equalTo(WEST));
        assertThat(game.getHandTrickCounts(), equalTo((Map) ImmutableMap.of(SOUTH, 0, WEST, 1, EAST, 0, WIDOW, 0)));

        game.makeTurn(HEART_JACK);
        assertThat(game.getTurn(), equalTo(EAST));
        assertThat(game.getCenterCards(), equalTo((Map) ImmutableMap.of(HEART_JACK, WEST)));
        assertThat(game.getHandTrickCounts(), equalTo((Map) ImmutableMap.of(SOUTH, 0, WEST, 1, EAST, 0, WIDOW, 0)));

        assertTrue(game.undoTurn(), "Failed to undo WEST->HEART_JACK");
        assertThat(game.getTurn(), equalTo(WEST));
        assertTrue(game.getCenterCards().isEmpty());
        assertThat(game.getHandTrickCounts(), equalTo((Map) ImmutableMap.of(SOUTH, 0, WEST, 1, EAST, 0, WIDOW, 0)));

        assertTrue(game.undoTurn(), "Failed to undo EAST->CLUB_8");
        assertThat(game.getTurn(), equalTo(EAST));
        assertThat(game.getCenterCards(), equalTo((Map) ImmutableMap.of(CLUB_7, SOUTH, CLUB_JACK, WEST)));
        assertThat(game.getHandTrickCounts(), equalTo((Map) ImmutableMap.of(SOUTH, 0, WEST, 0, EAST, 0, WIDOW, 0)));

        assertTrue(game.undoTurn(), "Failed to undo WEST->CLUB_JACK");
        assertThat(game.getTurn(), equalTo(WEST));
        assertThat(game.getCenterCards(), equalTo((Map) ImmutableMap.of(CLUB_7, SOUTH)));

        assertTrue(game.undoTurn(), "Failed to undo SOUTH->CLUB_7");
        assertThat(game.getTurn(), equalTo(SOUTH));
        assertTrue(game.getCenterCards().isEmpty());
        assertFalse(game.undoTurn(), "Shouldn't undo");

        assertTrue(game.redoTurn(), "Failed to redo SOUTH->CLUB_7");
        assertThat(game.getTurn(), equalTo(WEST));
        assertThat(game.getCenterCards(), equalTo((Map) ImmutableMap.of(CLUB_7, SOUTH)));

        assertTrue(game.redoTurn(), "Failed to redo WEST->CLUB_JACK");
        assertThat(game.getTurn(), equalTo(EAST));
        assertThat(game.getCenterCards(), equalTo((Map) ImmutableMap.of(CLUB_7, SOUTH, CLUB_JACK, WEST)));
        assertThat(game.getHandTrickCounts(), equalTo((Map) ImmutableMap.of(SOUTH, 0, WEST, 0, EAST, 0, WIDOW, 0)));

        assertTrue(game.redoTurn(), "Failed to redo EAST->CLUB_8");
        assertThat(game.getTurn(), equalTo(WEST));
        assertThat(game.getCenterCards(), equalTo((Map) ImmutableMap.of()));
        assertThat(game.getHandTrickCounts(), equalTo((Map) ImmutableMap.of(SOUTH, 0, WEST, 1, EAST, 0, WIDOW, 0)));

        assertTrue(game.redoTurn(), "Failed to redo WEST->HEART_JACK");
        assertThat(game.getTurn(), equalTo(EAST));
        assertThat(game.getCenterCards(), equalTo((Map) ImmutableMap.of(HEART_JACK, WEST)));
        assertThat(game.getHandTrickCounts(), equalTo((Map) ImmutableMap.of(SOUTH, 0, WEST, 1, EAST, 0, WIDOW, 0)));
    }

    @Test
    public void testUndoSluffRedo() throws Exception {
        game.makeTurn(CLUB_ACE);
        game.makeTurn(CLUB_JACK);
        game.makeTurn(CLUB_8);
        assertTrue(game.sluffTrick(), "Failed to sluff trick");
        game.makeTurn(CLUB_KING);
        assertTrue(game.undoTurn(), "Failed to undo SOUTH->CLUB_KING");
        assertThat(game.getCenterCards(), equalTo((Map) ImmutableMap.of()));
        assertTrue(game.undoTurn(), "Failed to undo EAST->CLUB_8");
        assertThat(game.getCenterCards(), equalTo((Map) ImmutableMap.of(CLUB_ACE, SOUTH, CLUB_JACK, WEST)));
        assertTrue(game.redoTurn(), "Failed to redo EAST->CLUB_8");
        assertThat(game.getCenterCards(), equalTo((Map) ImmutableMap.of()));
    }

    @Test
    public void testHasUndoTurns() throws Exception {
        assertFalse(game.hasUndoTurns());
        game.makeTurn(CLUB_ACE);
        assertTrue(game.hasUndoTurns());
        game.makeTurn(CLUB_JACK);
        assertTrue(game.hasUndoTurns());
        game.makeTurn(CLUB_8);
        assertTrue(game.hasUndoTurns());
        game.sluffTrick();
        assertTrue(game.hasUndoTurns());
        game.undoTurn(); // Undo SOUTH->CLUB_ACE
        assertTrue(game.hasUndoTurns());
        game.undoTurn(); // Undo WEST->CLUB_JACK
        assertTrue(game.hasUndoTurns());
        game.undoTurn(); // Undo EAST->CLUB_8
        assertFalse(game.hasUndoTurns());
    }

    @Test
    public void testHasRedoTurns() throws Exception {
        assertFalse(game.hasRedoTurns());
        game.makeTurn(CLUB_ACE);
        assertFalse(game.hasRedoTurns());
        game.undoTurn(); // Undo SOUTH->CLUB_ACE
        assertTrue(game.hasRedoTurns());
    }

    @Test
    public void testHasRedoTurns_WithSluff() throws Exception {
        assertFalse(game.hasRedoTurns());
        game.makeTurn(CLUB_ACE);
        assertFalse(game.hasRedoTurns());
        game.makeTurn(CLUB_JACK);
        assertFalse(game.hasRedoTurns());
        game.makeTurn(CLUB_8);
        assertFalse(game.hasRedoTurns());
        game.sluffTrick();
        assertFalse(game.hasRedoTurns());
        game.undoTurn(); // Undo SOUTH->CLUB_ACE
        assertTrue(game.hasRedoTurns());
        game.undoTurn(); // Undo WEST->CLUB_JACK
        assertTrue(game.hasRedoTurns());
        game.undoTurn(); // Undo EAST->CLUB_8
        assertTrue(game.hasRedoTurns());
        game.makeTurn(CLUB_ACE); // By making manual turn we reset redo queue
        assertFalse(game.hasRedoTurns());
    }

    @Test
    public void testReset() throws Exception {
        game.makeTurn(CLUB_ACE);
        game.makeTurn(CLUB_JACK);
        game.makeTurn(CLUB_8);
        game.sluffTrick();
        game.makeTurn(CLUB_KING);
        game.reset();
        assertFalse(game.hasUndoTurns());
        assertFalse(game.hasRedoTurns());
        assertThat(game.getTurn(), equalTo(SOUTH));
        assertThat(game.getHandTrickCounts(), equalTo((Map) ImmutableMap.of(SOUTH, 0, WEST, 0, EAST, 0, WIDOW, 0)));
        assertThat(game.getCenterCards(), equalTo((Map) ImmutableMap.of()));
    }

    @Test
    public void testToDeal() throws Exception {
        game.makeTurn(CLUB_ACE);
        game.makeTurn(CLUB_JACK);
        game.makeTurn(CLUB_8);
        game.sluffTrick();
        game.makeTurn(CLUB_KING);

        Deal deal = game.toDeal(name, description);
        Game clonedGame = new Game(deal);
        assertReflectionEquals(game, clonedGame);
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