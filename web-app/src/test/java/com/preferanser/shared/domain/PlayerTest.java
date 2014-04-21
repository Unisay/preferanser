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
import com.preferanser.shared.domain.exception.GameException;
import com.preferanser.shared.domain.exception.IllegalSuitException;
import com.preferanser.shared.domain.exception.NotInTurnException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Map;

import static com.google.common.collect.Sets.newHashSet;
import static com.preferanser.shared.domain.Card.*;
import static com.preferanser.shared.domain.Hand.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.testng.Assert.*;
import static org.unitils.reflectionassert.ReflectionAssert.assertLenientEquals;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

/**
 * Unit test for the Player
 */
public class PlayerTest {

    private Player player;
    private Widow widow;
    private String name;
    private String description;
    private Map<Hand, Contract> handContractMap;
    private LinkedHashMultimap<Hand, Card> handCardMultimap;

    @BeforeMethod
    public void setUp() throws Exception {
        name = "name";
        description = "description";
        widow = new Widow(DIAMOND_ACE, HEART_ACE);
        handContractMap = buildHandContractMap();
        handCardMultimap = buildHandCardMultimap();
        player = buildPlayer(name, description, Players.THREE, widow, handContractMap, SOUTH, handCardMultimap);
    }

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "^No hand holds CLUB_QUEEN.*$")
    public void testMakeTurn_FromHandWrongCard() throws Exception {
        player.makeTurn(CLUB_QUEEN);
    }

    @Test
    public void testMakeTurn_NotInTurn() throws Exception {
        try {
            player.makeTurn(CLUB_JACK);
            fail("NotInTurnException must have been thrown!");
        } catch (NotInTurnException e) {
            assertThat(e.getFromHand(), equalTo(WEST));
            assertThat(e.getCurrentHand(), equalTo(SOUTH));
            assertThat(e.getMessage(), equalTo("WEST attempted to make turn (CLUB_JACK) while current turn does SOUTH"));
        }
    }

    @Test
    public void testMakeTurn_WrongTrickSuit() throws Exception {
        handCardMultimap.clear();
        handCardMultimap.put(SOUTH, CLUB_ACE);
        handCardMultimap.put(WEST, HEART_JACK);
        handCardMultimap.put(WEST, CLUB_KING);

        player = buildPlayer(name, description, Players.THREE, widow, handContractMap, SOUTH, handCardMultimap);

        player.makeTurn(CLUB_ACE);
        try {
            player.makeTurn(HEART_JACK);
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

        player = buildPlayer(name, description, Players.THREE, widow, handContractMap, SOUTH, handCardMultimap);

        player.makeTurn(CLUB_ACE);
        try {
            player.makeTurn(SPADE_ACE);
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
        handCardMultimap.put(WEST, HEART_JACK);
        handCardMultimap.put(WEST, SPADE_ACE);

        player = buildPlayer(name, description, Players.THREE, widow, handContractMap, SOUTH, handCardMultimap);

        player.makeTurn(CLUB_ACE);
        try {
            player.makeTurn(HEART_JACK);
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
        handCardMultimap.put(WEST, HEART_JACK);
        handCardMultimap.put(WEST, DIAMOND_ACE);

        player = buildPlayer(name, description, Players.THREE, widow, handContractMap, SOUTH, handCardMultimap);

        player.makeTurn(CLUB_ACE);
        player.makeTurn(HEART_JACK);
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
        handCardMultimap.put(WEST, HEART_JACK);
        handCardMultimap.put(WEST, SPADE_ACE);

        player = buildPlayer(name, description, Players.THREE, widow, handContractMap, SOUTH, handCardMultimap);

        player.makeTurn(CLUB_ACE);
        player.makeTurn(HEART_JACK);
    }

    @Test
    public void testMakeTurn_DuplicateGameTurn() throws Exception {
        player.makeTurn(CLUB_ACE);
        player.makeTurn(CLUB_9);
        player.makeTurn(CLUB_8);
        try {
            player.makeTurn(CLUB_KING);
            fail("DuplicateGameTurnException must have been thrown!");
        } catch (DuplicateGameTurnException e) {
            assertThat(e.getFromHand(), equalTo(SOUTH));
            assertThat(e.getCenterCardHandMap(), equalTo(player.getCenterCards()));
        }
    }

    @Test
    public void testIsTrickClosed_ThreePlayers() throws Exception {
        player.makeTurn(DIAMOND_9);     // SOUTH
        assertFalse(player.isTrickClosed());

        player.makeTurn(DIAMOND_JACK);  // WEST
        assertFalse(player.isTrickClosed());

        player.makeTurn(DIAMOND_7);     // EAST
        assertTrue(player.isTrickClosed());
    }

    @Test
    public void testIsTrickClosed_ThreePlayers_Raspass() throws Exception {
        testIsTrickClosed_Raspass(Players.THREE);
    }

    @Test
    public void testIsTrickClosed_FourPlayers_Raspass() throws Exception {
        testIsTrickClosed_Raspass(Players.FOUR);
    }

    private void testIsTrickClosed_Raspass(Players players) throws GameException {
        handContractMap = ImmutableMap.of(EAST, Contract.PASS, SOUTH, Contract.PASS, WEST, Contract.PASS);

        player = buildPlayer(name, description, players, widow, handContractMap, SOUTH, handCardMultimap);

        player.makeTurn(DIAMOND_ACE);   // WIDOW
        assertFalse(player.isTrickClosed());

        player.makeTurn(DIAMOND_9);     // SOUTH
        assertFalse(player.isTrickClosed());

        player.makeTurn(DIAMOND_JACK);  // WEST
        assertFalse(player.isTrickClosed());

        player.makeTurn(DIAMOND_7);     // EAST
        assertTrue(player.isTrickClosed());

        player.sluffTrick();

        player.makeTurn(HEART_ACE);     // WIDOW
        assertFalse(player.isTrickClosed());

        player.makeTurn(CLUB_ACE);      // SOUTH
        assertFalse(player.isTrickClosed());

        player.makeTurn(HEART_JACK);    // WEST
        assertFalse(player.isTrickClosed());

        player.makeTurn(CLUB_8);        // EAST
        assertTrue(player.isTrickClosed());

        player.sluffTrick();

        player.makeTurn(CLUB_KING);     // SOUTH
        assertFalse(player.isTrickClosed());

        player.makeTurn(CLUB_JACK);     // WEST
        assertFalse(player.isTrickClosed());

        player.makeTurn(DIAMOND_8);     // EAST
        assertTrue(player.isTrickClosed());
    }

    @Test
    public void testGetTurn() throws Exception {
        assertThat(player.getTurn(), equalTo(SOUTH));
        player.makeTurn(CLUB_ACE);
        assertThat(player.getTurn(), equalTo(WEST));
        player.makeTurn(CLUB_9);
        assertThat(player.getTurn(), equalTo(EAST));
        player.makeTurn(CLUB_8);
        assertThat(player.getTurn(), equalTo(SOUTH));
    }

    @Test
    public void testGetTurn_Raspass() throws Exception {
        handContractMap = ImmutableMap.of(EAST, Contract.PASS, SOUTH, Contract.PASS, WEST, Contract.PASS);

        player = buildPlayer(name, description, Players.FOUR, widow, handContractMap, SOUTH, handCardMultimap);

        assertThat(player.getTurn(), equalTo(Hand.WIDOW));
        player.makeTurn(DIAMOND_ACE);
        assertThat(player.getTurn(), equalTo(Hand.SOUTH));
        player.makeTurn(DIAMOND_9);
        assertThat(player.getTurn(), equalTo(WEST));
        player.makeTurn(DIAMOND_JACK);
        assertThat(player.getTurn(), equalTo(EAST));
        player.makeTurn(DIAMOND_7);
        assertThat(player.getTurn(), equalTo(WIDOW));
    }

    @Test
    public void testGetTrump() throws Exception {
        assertTrue(player.getTrump().isPresent());
        assertThat(player.getTrump().get(), equalTo(Suit.SPADE));
    }

    @Test
    public void testGetTrump_NoTrumpPlayingContract() throws Exception {
        handContractMap = ImmutableMap.of(SOUTH, Contract.SIX_NO_TRUMP, EAST, Contract.PASS, WEST, Contract.WHIST);
        player = buildPlayer(name, description, Players.THREE, widow, handContractMap, SOUTH, handCardMultimap);

        assertFalse(player.getTrump().isPresent());
    }

    @Test
    public void testGetTrump_NoTrumpNotPlayingContract() throws Exception {
        handContractMap = ImmutableMap.of(SOUTH, Contract.PASS, EAST, Contract.PASS, WEST, Contract.PASS);
        player = buildPlayer(name, description, Players.THREE, widow, handContractMap, SOUTH, handCardMultimap);

        assertFalse(player.getTrump().isPresent());
    }

    @Test
    public void testSluffTrick() throws Exception {
        assertFalse(player.sluffTrick());
        player.makeTurn(CLUB_ACE);
        assertFalse(player.sluffTrick());
        player.makeTurn(CLUB_JACK);
        assertFalse(player.sluffTrick());
        player.makeTurn(CLUB_8);
        assertTrue(player.sluffTrick());

        assertTrue(player.getCenterCards().isEmpty());
        assertThat(player.getTurn(), equalTo(SOUTH));
        assertReflectionEquals(ImmutableMap.of(SOUTH, 1, EAST, 0, WEST, 0, WIDOW, 0), player.getHandTrickCounts());
    }

    @Test
    public void testGetDisabledCards() throws Exception {
        assertLenientEquals(
            newHashSet(CLUB_9, CLUB_JACK, SPADE_8, DIAMOND_JACK, DIAMOND_7, DIAMOND_QUEEN, DIAMOND_8, HEART_JACK, CLUB_8),
            player.getDisabledCards());
        player.makeTurn(CLUB_ACE);
        assertLenientEquals(
            newHashSet(SPADE_8, DIAMOND_9, DIAMOND_10, CLUB_8, DIAMOND_QUEEN, CLUB_KING, DIAMOND_JACK, HEART_JACK, DIAMOND_7, DIAMOND_8),
            player.getDisabledCards());
        player.makeTurn(CLUB_JACK);
        assertLenientEquals(
            newHashSet(DIAMOND_10, CLUB_KING, DIAMOND_9, DIAMOND_JACK, DIAMOND_8, HEART_JACK, SPADE_8, CLUB_9, DIAMOND_QUEEN, DIAMOND_7),
            player.getDisabledCards());
        player.makeTurn(CLUB_8);
        assertLenientEquals(
            newHashSet(DIAMOND_9, CLUB_KING, DIAMOND_QUEEN, HEART_JACK, DIAMOND_8, DIAMOND_7, CLUB_9, DIAMOND_10, DIAMOND_JACK, SPADE_8),
            player.getDisabledCards());
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

        player = buildPlayer(name, description, Players.THREE, widow, handContractMap, SOUTH, handCardMultimap);

        player.makeTurn(CLUB_7);    // SOUTH
        player.makeTurn(CLUB_JACK); // WEST <-- wins
        player.makeTurn(CLUB_8);    // EAST

        assertTrue(player.sluffTrick(), "Failed to sluff trick");
        assertThat(player.getTurn(), equalTo(WEST));
        assertThat(player.getHandTrickCounts(), equalTo((Map) ImmutableMap.of(SOUTH, 0, WEST, 1, EAST, 0, WIDOW, 0)));

        player.makeTurn(HEART_JACK); // WEST
        assertThat(player.getTurn(), equalTo(EAST));
        assertThat(player.getCenterCards(), equalTo((Map) ImmutableMap.of(HEART_JACK, WEST)));
        assertThat(player.getHandTrickCounts(), equalTo((Map) ImmutableMap.of(SOUTH, 0, WEST, 1, EAST, 0, WIDOW, 0)));

        assertTrue(player.undoTurn(), "Failed to undo WEST->HEART_JACK");
        assertThat(player.getTurn(), equalTo(WEST));
        assertTrue(player.getCenterCards().isEmpty());
        assertThat(player.getHandTrickCounts(), equalTo((Map) ImmutableMap.of(SOUTH, 0, WEST, 1, EAST, 0, WIDOW, 0)));

        assertTrue(player.undoTurn(), "Failed to undo EAST->CLUB_8");
        assertThat(player.getTurn(), equalTo(EAST));
        assertThat(player.getCenterCards(), equalTo((Map) ImmutableMap.of(CLUB_7, SOUTH, CLUB_JACK, WEST)));
        assertThat(player.getHandTrickCounts(), equalTo((Map) ImmutableMap.of(SOUTH, 0, WEST, 0, EAST, 0, WIDOW, 0)));

        assertTrue(player.undoTurn(), "Failed to undo WEST->CLUB_JACK");
        assertThat(player.getTurn(), equalTo(WEST));
        assertThat(player.getCenterCards(), equalTo((Map) ImmutableMap.of(CLUB_7, SOUTH)));

        assertTrue(player.undoTurn(), "Failed to undo SOUTH->CLUB_7");
        assertThat(player.getTurn(), equalTo(SOUTH));
        assertTrue(player.getCenterCards().isEmpty());
        assertFalse(player.undoTurn(), "Shouldn't undo");

        assertTrue(player.redoTurn(), "Failed to redo SOUTH->CLUB_7");
        assertThat(player.getTurn(), equalTo(WEST));
        assertThat(player.getCenterCards(), equalTo((Map) ImmutableMap.of(CLUB_7, SOUTH)));

        assertTrue(player.redoTurn(), "Failed to redo WEST->CLUB_JACK");
        assertThat(player.getTurn(), equalTo(EAST));
        assertThat(player.getCenterCards(), equalTo((Map) ImmutableMap.of(CLUB_7, SOUTH, CLUB_JACK, WEST)));
        assertThat(player.getHandTrickCounts(), equalTo((Map) ImmutableMap.of(SOUTH, 0, WEST, 0, EAST, 0, WIDOW, 0)));

        assertTrue(player.redoTurn(), "Failed to redo EAST->CLUB_8");
        assertThat(player.getTurn(), equalTo(WEST));
        assertThat(player.getCenterCards(), equalTo((Map) ImmutableMap.of()));
        assertThat(player.getHandTrickCounts(), equalTo((Map) ImmutableMap.of(SOUTH, 0, WEST, 1, EAST, 0, WIDOW, 0)));

        assertTrue(player.redoTurn(), "Failed to redo WEST->HEART_JACK");
        assertThat(player.getTurn(), equalTo(EAST));
        assertThat(player.getCenterCards(), equalTo((Map) ImmutableMap.of(HEART_JACK, WEST)));
        assertThat(player.getHandTrickCounts(), equalTo((Map) ImmutableMap.of(SOUTH, 0, WEST, 1, EAST, 0, WIDOW, 0)));
    }

    @Test
    public void testUndoSluffRedo() throws Exception {
        player.makeTurn(CLUB_ACE);
        player.makeTurn(CLUB_JACK);
        player.makeTurn(CLUB_8);
        assertTrue(player.sluffTrick(), "Failed to sluff trick");
        player.makeTurn(CLUB_KING);
        assertTrue(player.undoTurn(), "Failed to undo SOUTH->CLUB_KING");
        assertThat(player.getCenterCards(), equalTo((Map) ImmutableMap.of()));
        assertTrue(player.undoTurn(), "Failed to undo EAST->CLUB_8");
        assertThat(player.getCenterCards(), equalTo((Map) ImmutableMap.of(CLUB_ACE, SOUTH, CLUB_JACK, WEST)));
        assertTrue(player.redoTurn(), "Failed to redo EAST->CLUB_8");
        assertThat(player.getCenterCards(), equalTo((Map) ImmutableMap.of()));
    }

    @Test
    public void testHasUndoTurns() throws Exception {
        assertFalse(player.hasUndoTurns());
        player.makeTurn(CLUB_ACE);
        assertTrue(player.hasUndoTurns());
        player.makeTurn(CLUB_JACK);
        assertTrue(player.hasUndoTurns());
        player.makeTurn(CLUB_8);
        assertTrue(player.hasUndoTurns());
        player.sluffTrick();
        assertTrue(player.hasUndoTurns());
        player.undoTurn(); // Undo SOUTH->CLUB_ACE
        assertTrue(player.hasUndoTurns());
        player.undoTurn(); // Undo WEST->CLUB_JACK
        assertTrue(player.hasUndoTurns());
        player.undoTurn(); // Undo EAST->CLUB_8
        assertFalse(player.hasUndoTurns());
    }

    @Test
    public void testHasRedoTurns() throws Exception {
        assertFalse(player.hasRedoTurns());
        player.makeTurn(CLUB_ACE);
        assertFalse(player.hasRedoTurns());
        player.undoTurn(); // Undo SOUTH->CLUB_ACE
        assertTrue(player.hasRedoTurns());
    }

    @Test
    public void testHasRedoTurns_WithSluff() throws Exception {
        assertFalse(player.hasRedoTurns());
        player.makeTurn(CLUB_ACE);
        assertFalse(player.hasRedoTurns());
        player.makeTurn(CLUB_JACK);
        assertFalse(player.hasRedoTurns());
        player.makeTurn(CLUB_8);
        assertFalse(player.hasRedoTurns());
        player.sluffTrick();
        assertFalse(player.hasRedoTurns());
        player.undoTurn(); // Undo SOUTH->CLUB_ACE
        assertTrue(player.hasRedoTurns());
        player.undoTurn(); // Undo WEST->CLUB_JACK
        assertTrue(player.hasRedoTurns());
        player.undoTurn(); // Undo EAST->CLUB_8
        assertTrue(player.hasRedoTurns());
        player.makeTurn(CLUB_ACE); // By making manual turn we reset redo queue
        assertFalse(player.hasRedoTurns());
    }

    @Test
    public void testReset() throws Exception {
        player.makeTurn(CLUB_ACE);
        player.makeTurn(CLUB_JACK);
        player.makeTurn(CLUB_8);
        player.sluffTrick();
        player.makeTurn(CLUB_KING);
        player.reset();
        assertFalse(player.hasUndoTurns());
        assertFalse(player.hasRedoTurns());
        assertThat(player.getTurn(), equalTo(SOUTH));
        assertThat(player.getHandTrickCounts(), equalTo((Map) ImmutableMap.of(SOUTH, 0, WEST, 0, EAST, 0, WIDOW, 0)));
        assertThat(player.getCenterCards(), equalTo((Map) ImmutableMap.of()));
    }

    @Test
    public void testToDeal() throws Exception {
        player.makeTurn(CLUB_ACE);
        player.makeTurn(CLUB_JACK);
        player.makeTurn(CLUB_8);
        player.sluffTrick();
        player.makeTurn(CLUB_KING);

        Deal deal = player.toDeal();
        Player clonedPlayer = new Player(deal);
        assertReflectionEquals(player, clonedPlayer);
    }

    private Map<Hand, Contract> buildHandContractMap() {
        return ImmutableMap.of(
            EAST, Contract.PASS,
            SOUTH, Contract.SIX_SPADE,
            WEST, Contract.WHIST
        );
    }

    private LinkedHashMultimap<Hand, Card> buildHandCardMultimap() {
        LinkedHashMultimap<Hand, Card> multimap = LinkedHashMultimap.create();

        multimap.put(EAST, CLUB_8);
        multimap.put(EAST, SPADE_8);
        multimap.put(EAST, DIAMOND_7);
        multimap.put(EAST, DIAMOND_8);

        multimap.put(SOUTH, CLUB_ACE);
        multimap.put(SOUTH, CLUB_KING);
        multimap.put(SOUTH, DIAMOND_9);
        multimap.put(SOUTH, DIAMOND_10);

        multimap.put(WEST, CLUB_JACK);
        multimap.put(WEST, CLUB_9);
        multimap.put(WEST, HEART_JACK);
        multimap.put(WEST, DIAMOND_JACK);
        multimap.put(WEST, DIAMOND_QUEEN);

        return multimap;
    }


    private Player buildPlayer(
        String name,
        String description,
        Players players,
        Widow widow,
        Map<Hand, Contract> handContractMap,
        Hand firstTurn,
        LinkedHashMultimap<Hand, Card> handCardMultimap
    ) {
        Deal deal = new Deal();
        deal.setName(name);
        deal.setDescription(description);
        deal.setPlayers(players);
        deal.setWidow(widow);
        deal.setFirstTurn(firstTurn);
        deal.setSouthCards(handCardMultimap.get(SOUTH));
        deal.setWestCards(handCardMultimap.get(WEST));
        deal.setEastCards(handCardMultimap.get(EAST));
        deal.setSouthContract(handContractMap.get(SOUTH));
        deal.setWestContract(handContractMap.get(WEST));
        deal.setEastContract(handContractMap.get(EAST));
        deal.setCurrentTrickIndex(0);
        return new Player(deal);
    }
}