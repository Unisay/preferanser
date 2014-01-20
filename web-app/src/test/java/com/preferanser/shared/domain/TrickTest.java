package com.preferanser.shared.domain;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.preferanser.shared.util.EnumRotator;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Map;

import static com.preferanser.shared.domain.Card.*;
import static com.preferanser.shared.domain.Hand.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class TrickTest {

    private Trick trick;
    private EnumRotator<Hand> turnRotator;

    @BeforeMethod
    public void setUp() throws Exception {
        turnRotator = new EnumRotator<Hand>(Hand.values(), SOUTH);
        turnRotator.setSkipValues(Hand.NORTH);
        trick = new Trick(Players.THREE, turnRotator);
    }

    @Test
    public void testGetHandCard() throws Exception {
        assertThat(trick.getHandCard(SOUTH), nullValue());
        trick.applyTurn(SOUTH, Card.CLUB_ACE);
        assertThat(trick.getHandCard(SOUTH), equalTo(Card.CLUB_ACE));
    }

    @Test
    public void testHasCardFrom() throws Exception {
        assertFalse(trick.hasCardFrom(SOUTH));
        trick.applyTurn(SOUTH, CLUB_ACE);
        assertTrue(trick.hasCardFrom(SOUTH));
    }

    @Test(expectedExceptions = IllegalStateException.class,
        expectedExceptionsMessageRegExp = "Can't turn from EAST as current turn makes SOUTH")
    public void testApplyTurn_WrongHand() throws Exception {
        trick.applyTurn(EAST, CLUB_ACE);
    }

    @Test(expectedExceptions = IllegalArgumentException.class,
        expectedExceptionsMessageRegExp = "SOUTH can't make a turn \\(CLUB_KING\\) - already made its turn \\(CLUB_ACE\\)")
    public void testApplyTurn_Duplicate() throws Exception {
        trick = new Trick(Players.THREE, turnRotator, ImmutableMap.<Card, Hand>of(CLUB_ACE, SOUTH));
        trick.applyTurn(SOUTH, CLUB_KING);
    }

    @Test(expectedExceptions = IllegalArgumentException.class,
        expectedExceptionsMessageRegExp = "SOUTH can't make a turn \\(CLUB_9\\) - already made its turn \\(CLUB_KING\\)")
    public void testApplyTurn_ExtraTurn() throws Exception {
        trick.applyTurn(SOUTH, CLUB_KING);
        trick.applyTurn(WEST, CLUB_10);
        trick.applyTurn(EAST, CLUB_7);
        trick.applyTurn(SOUTH, Card.CLUB_9);
    }

    @Test
    public void testHasUndoTurns() throws Exception {
        assertFalse(trick.hasUndoTurns());
        trick.applyTurn(SOUTH, CLUB_KING);
        assertTrue(trick.hasUndoTurns());
        trick.undoTurn();
        assertFalse(trick.hasUndoTurns());
    }

    @Test
    public void testHasRedoTurns() throws Exception {
        trick.applyTurn(SOUTH, CLUB_KING);
        trick.undoTurn();
        assertTrue(trick.hasRedoTurns());
        trick.applyTurn(SOUTH, CLUB_ACE);
        assertFalse(trick.hasRedoTurns());
    }

    @Test
    public void testUndoTurn() throws Exception {
        trick.applyTurn(SOUTH, CLUB_KING);
        assertThat(trick.asMap(), equalTo((Map) ImmutableMap.of(CLUB_KING, SOUTH)));
        assertThat(trick.getTurn(), equalTo(WEST));

        trick.undoTurn();

        assertThat(trick.getTurn(), equalTo(SOUTH));
        assertTrue(trick.asMap().isEmpty());
    }

    @Test(expectedExceptions = IllegalStateException.class, expectedExceptionsMessageRegExp = "Can't revert last turn in the trick because trick is empty")
    public void testUndoTurn_EmptyTrick() throws Exception {
        trick.undoTurn();
    }

    @Test
    public void testRedoTurn() throws Exception {
        trick.applyTurn(SOUTH, CLUB_KING);
        trick.undoTurn();
        trick.redoTurn();
        assertThat(trick.getTurn(), equalTo(WEST));
        assertThat(trick.asMap(), equalTo((Map) ImmutableMap.of(CLUB_KING, SOUTH)));
    }

    @Test(expectedExceptions = IllegalStateException.class, expectedExceptionsMessageRegExp = "There is no turns in the trick to redo")
    public void testRedoTurn_EmptyTrick() throws Exception {
        trick.redoTurn();
    }

    @DataProvider(name = "trickWinners")
    public Object[][] createData1() {
        return new Object[][]{
            {Optional.absent(), ImmutableMap.of(SOUTH, CLUB_JACK, WEST, CLUB_ACE, EAST, CLUB_7), Optional.of(WEST)},
            {Optional.absent(), ImmutableMap.of(SOUTH, CLUB_JACK, WEST, SPADE_ACE, EAST, HEART_KING), Optional.of(SOUTH)},
            {Optional.absent(), ImmutableMap.of(SOUTH, CLUB_JACK, WEST, DIAMOND_10, EAST, CLUB_QUEEN), Optional.of(EAST)},

            {Optional.of(Suit.CLUB), ImmutableMap.of(SOUTH, CLUB_JACK, WEST, CLUB_ACE, EAST, CLUB_7), Optional.of(WEST)},
            {Optional.of(Suit.CLUB), ImmutableMap.of(SOUTH, CLUB_JACK, WEST, SPADE_ACE, EAST, HEART_KING), Optional.of(SOUTH)},
            {Optional.of(Suit.CLUB), ImmutableMap.of(SOUTH, CLUB_JACK, WEST, DIAMOND_10, EAST, CLUB_QUEEN), Optional.of(EAST)},

            {Optional.of(Suit.CLUB), ImmutableMap.of(SOUTH, SPADE_10, WEST, SPADE_ACE, EAST, CLUB_7), Optional.of(EAST)},
            {Optional.of(Suit.CLUB), ImmutableMap.of(SOUTH, SPADE_10, WEST, CLUB_ACE, EAST, CLUB_7), Optional.of(WEST)},
            {Optional.of(Suit.CLUB), ImmutableMap.of(SOUTH, SPADE_10, WEST, SPADE_JACK, EAST, SPADE_7), Optional.of(WEST)},

            {Optional.of(Suit.CLUB), ImmutableMap.of(SOUTH, SPADE_10, WEST, DIAMOND_10, EAST, DIAMOND_7), Optional.of(SOUTH)},
        };
    }

    @Test(dataProvider = "trickWinners")
    public void testDetermineTrickWinner(Optional<Suit> trump, Map<Hand, Card> cards, Optional<Hand> winner) throws Exception {
        for (Map.Entry<Hand, Card> entry : cards.entrySet())
            trick.applyTurn(entry.getKey(), entry.getValue());
        assertThat(trick.determineTrickWinner(trump), equalTo(winner));
    }

    @Test
    public void testGetTurn() throws Exception {
        assertThat(trick.getTurn(), equalTo(SOUTH));
        trick.applyTurn(SOUTH, CLUB_KING);
        assertThat(trick.getTurn(), equalTo(WEST));
        trick.applyTurn(WEST, CLUB_QUEEN);
        assertThat(trick.getTurn(), equalTo(EAST));
        trick.applyTurn(EAST, CLUB_JACK);
        assertThat(trick.getTurn(), equalTo(SOUTH));
    }

    @Test
    public void testGetSuit() throws Exception {
        assertFalse(trick.getSuit().isPresent());
        trick.applyTurn(SOUTH, CLUB_7);
        trick.applyTurn(WEST, DIAMOND_10);
        trick.applyTurn(EAST, SPADE_10);
        assertThat(trick.getSuit(), equalTo(Optional.of(Suit.CLUB)));
    }

    @Test
    public void testAsMap() throws Exception {
        trick.applyTurn(SOUTH, CLUB_7);
        trick.applyTurn(WEST, DIAMOND_10);
        trick.applyTurn(EAST, SPADE_10);
        assertThat(trick.asMap(), equalTo((Map) ImmutableMap.of(CLUB_7, SOUTH, DIAMOND_10, WEST, SPADE_10, EAST)));
    }

    @Test
    public void testIsEmpty() throws Exception {
        assertTrue(trick.isEmpty());
        trick.applyTurn(SOUTH, CLUB_7);
        assertFalse(trick.isEmpty());
    }

    @Test
    public void testIsOpen() throws Exception {
        assertTrue(trick.isOpen());
        trick.applyTurn(SOUTH, CLUB_7);
        assertTrue(trick.isOpen());
        trick.applyTurn(WEST, DIAMOND_10);
        assertTrue(trick.isOpen());
        trick.applyTurn(EAST, SPADE_10);
        assertFalse(trick.isOpen());
    }

    @Test
    public void testIsClosed() throws Exception {
        assertFalse(trick.isClosed());
        trick.applyTurn(SOUTH, CLUB_7);
        assertFalse(trick.isClosed());
        trick.applyTurn(WEST, DIAMOND_10);
        assertFalse(trick.isClosed());
        trick.applyTurn(EAST, SPADE_10);
        assertTrue(trick.isClosed());
    }
}
