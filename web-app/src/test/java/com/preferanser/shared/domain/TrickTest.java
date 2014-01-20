package com.preferanser.shared.domain;

import com.preferanser.shared.util.EnumRotator;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class TrickTest {

    private Trick trick;
    private EnumRotator<Hand> turnRotator;

    @BeforeMethod
    public void setUp() throws Exception {
        turnRotator = new EnumRotator<Hand>(Hand.values(), Hand.SOUTH);
        trick = new Trick(Players.THREE, turnRotator);
    }

    @Test
    public void testGetHandCard() throws Exception {

    }

    @Test
    public void testHasCardFrom() throws Exception {

    }

    @Test
    public void testApplyTurn() throws Exception {

    }

    @Test
    public void testHasUndoTurns() throws Exception {

    }

    @Test
    public void testHasRedoTurns() throws Exception {

    }

    @Test
    public void testUndoTurn() throws Exception {

    }

    @Test
    public void testRedoTurn() throws Exception {

    }

    @Test
    public void testDetermineTrickWinner() throws Exception {

    }

    @Test
    public void testGetTurn() throws Exception {

    }

    @Test
    public void testGetSuit() throws Exception {

    }

    @Test
    public void testAsMap() throws Exception {

    }

    @Test
    public void testIsEmpty() throws Exception {

    }

    @Test
    public void testIsOpen() throws Exception {

    }

    @Test
    public void testIsClosed() throws Exception {

    }
}
