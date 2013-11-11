package com.preferanser.domain;

import org.junit.Before;
import org.junit.Test;

import static com.preferanser.domain.Cardinal.*;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class GameBuilderTest {

    private Game.Builder gameBuilder;

    @Before
    public void setUp() throws Exception {
        gameBuilder = new Game.Builder()
                .threePlayerGame()
                .cardinalContract(NORTH, Contract.SIX_SPADE)
                .cardinalContract(EAST, Contract.WHIST)
                .cardinalContract(WEST, Contract.WHIST)
                .firstTurn(NORTH)
        ;
    }

    @Test
    public void testBuild_ThreePlayerGame() throws Exception {
        Game game = gameBuilder.threePlayerGame().build();
        assertThat(game.getNumPlayers(), equalTo(3));
    }

    @Test
    public void testBuild_FourPlayerGame() throws Exception {
        Game game = gameBuilder.fourPlayerGame().build();
        assertThat(game.getNumPlayers(), equalTo(4));
    }

}