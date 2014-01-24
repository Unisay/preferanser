package com.preferanser.shared.domain;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;

public class TurnTest {

    private Turn turn;

    @BeforeMethod
    public void setUp() throws Exception {
        turn = new Turn(Hand.EAST, Card.CLUB_ACE);
    }

    @Test
    public void testEquals_Positive() throws Exception {
        assertThat(new Turn(Hand.EAST, Card.CLUB_ACE), equalTo(turn));
    }

    @Test
    public void testEquals_Negative() throws Exception {
        assertThat(new Turn(Hand.EAST, Card.CLUB_10), not(equalTo(turn)));
        assertThat(new Turn(Hand.SOUTH, Card.CLUB_ACE), not(equalTo(turn)));
        assertThat(new Turn(Hand.SOUTH, Card.CLUB_10), not(equalTo(turn)));
    }

}
