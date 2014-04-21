package com.preferanser.shared.domain;

import com.google.common.collect.Lists;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Queue;

import static com.preferanser.shared.domain.Hand.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;

public class RaspassTurnRotatorTest {

    private @Mock EnumTurnRotator enumRotator;

    private RaspassTurnRotator raspassTurnRotator;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        raspassTurnRotator = new RaspassTurnRotator(enumRotator);
    }

    @Test
    public void testCurrent() throws Exception {
        when(enumRotator.current()).thenAnswer(new HandRotatorAnswer(SOUTH, WEST, EAST));

        verifyTurnSequence(raspassTurnRotator,
            WIDOW, SOUTH, WEST, EAST,
            WIDOW, SOUTH, WEST, EAST,
            SOUTH, WEST, EAST);
    }

    @Test
    public void testNext() throws Exception {
        raspassTurnRotator.next();
        verify(enumRotator, times(0)).next();
        raspassTurnRotator.next();
        verify(enumRotator, times(1)).next();
        raspassTurnRotator.next();
        verify(enumRotator, times(2)).next();
        raspassTurnRotator.next();
        verify(enumRotator, times(3)).next();
        verifyNoMoreInteractions(enumRotator);
    }

    @Test
    public void testPrev() throws Exception {
        raspassTurnRotator.next();
        verify(enumRotator, times(0)).next();
        raspassTurnRotator.next();
        verify(enumRotator, times(1)).next();
        raspassTurnRotator.next();
        verify(enumRotator, times(2)).next();
        raspassTurnRotator.next();
        verify(enumRotator, times(3)).next();

        raspassTurnRotator.prev();
        verify(enumRotator, times(1)).prev();
        raspassTurnRotator.prev();
        verify(enumRotator, times(2)).prev();
        raspassTurnRotator.prev();
        verify(enumRotator, times(3)).prev();
        raspassTurnRotator.prev();
        verify(enumRotator, times(3)).prev();

        verifyNoMoreInteractions(enumRotator);
    }

    @Test
    public void testProduceNextRotator_WestWinsLastWidowTrick() throws Exception {
        when(enumRotator.current()).thenAnswer(new HandRotatorAnswer(SOUTH, WEST, EAST));

        verifyTurnSequence(raspassTurnRotator, WIDOW, SOUTH, WEST, EAST);

        TurnRotator turnRotator = raspassTurnRotator.produceNextRotator(Hand.EAST);
        verifyTurnSequence(turnRotator, WIDOW, SOUTH, WEST, EAST);

        turnRotator = turnRotator.produceNextRotator(Hand.WEST);
        verifyTurnSequence(turnRotator, SOUTH, WEST, EAST);

        turnRotator = turnRotator.produceNextRotator(Hand.WEST);
        verifyTurnSequence(turnRotator, WEST, EAST, SOUTH);
    }

    @Test
    public void testProduceNextRotator_WidowWinsLastWidowTrick() throws Exception {
        when(enumRotator.current()).thenAnswer(new HandRotatorAnswer(SOUTH, WEST, EAST));

        verifyTurnSequence(raspassTurnRotator, WIDOW, SOUTH, WEST, EAST);

        TurnRotator turnRotator = raspassTurnRotator.produceNextRotator(Hand.EAST);
        verifyTurnSequence(turnRotator, WIDOW, SOUTH, WEST, EAST);

        turnRotator = turnRotator.produceNextRotator(Hand.WIDOW);
        verifyTurnSequence(turnRotator, SOUTH, WEST, EAST);
    }

    private void verifyTurnSequence(TurnRotator turnRotator, Hand... turns) {
        for (int i = 0; i < turns.length; i++) {
            assertThat("Assertion turnRotator.current(#" + (i + 1) + ") failed", turnRotator.current(), equalTo(turns[i]));
            turnRotator.next();
        }
    }

    private static class HandRotatorAnswer implements Answer<Hand> {
        private Queue<Hand> filo;

        private HandRotatorAnswer(Hand... hands) {
            this.filo = Lists.newLinkedList(Arrays.asList(hands));
        }

        @Override public Hand answer(InvocationOnMock invocation) throws Throwable {
            Hand hand = filo.remove();
            filo.add(hand);
            return hand;
        }
    }
}
