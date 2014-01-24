package com.preferanser.shared.domain;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Iterator;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class WidowTest {

    private Widow widow;

    @BeforeMethod
    public void setUp() throws Exception {
        widow = new Widow(Card.SPADE_ACE, Card.CLUB_ACE);
    }

    @Test
    public void testEquals() throws Exception {
        assertThat(widow, equalTo(new Widow(Card.SPADE_ACE, Card.CLUB_ACE)));
        assertThat(widow, equalTo(new Widow(Card.CLUB_ACE, Card.SPADE_ACE)));
    }

    @Test
    public void testConstructorWidow() throws Exception {
        Widow widowClone = new Widow(widow);
        assertThat(widowClone, equalTo(widow));
    }

    @Test
    public void testAsSet() throws Exception {
        assertThat(widow.asSet(), equalTo((Set<Card>) newHashSet(Card.SPADE_ACE, Card.CLUB_ACE)));
    }

    @Test
    public void testAsSet_OnlyFirstCard() throws Exception {
        widow.setCard2(null);
        assertThat(widow.asSet(), equalTo((Set<Card>) newHashSet(Card.SPADE_ACE)));
    }

    @Test
    public void testAsSet_OnlySecondCard() throws Exception {
        widow.setCard1(null);
        assertThat(widow.asSet(), equalTo((Set<Card>) newHashSet(Card.CLUB_ACE)));
    }

    @Test
    public void testAsSet_NoCards() throws Exception {
        widow.setCard1(null);
        widow.setCard2(null);
        assertThat(widow.asSet(), empty());
    }

    @Test
    public void testIterator_TwoCards() throws Exception {
        Set<Card> cardSet = newHashSet();
        for (Card card : widow)
            cardSet.add(card);
        assertThat(cardSet, equalTo((Set<Card>) newHashSet(Card.SPADE_ACE, Card.CLUB_ACE)));
    }

    @Test
    public void testIterator_NoCards() throws Exception {
        widow.setCard1(null);
        widow.setCard2(null);
        Iterator<Card> iterator = widow.iterator();
        assertFalse(iterator.hasNext());
    }

    @Test
    public void testIterator_NoFirstCard() throws Exception {
        widow.setCard1(null);
        Iterator<Card> iterator = widow.iterator();
        assertTrue(iterator.hasNext());
        assertThat(iterator.next(), equalTo(Card.CLUB_ACE));
        assertFalse(iterator.hasNext());
    }

    @Test
    public void testIterator_NoSecondCard() throws Exception {
        widow.setCard2(null);
        Iterator<Card> iterator = widow.iterator();
        assertTrue(iterator.hasNext());
        assertThat(iterator.next(), equalTo(Card.SPADE_ACE));
        assertFalse(iterator.hasNext());
    }

    @Test
    public void testHasTwoCards_Positive() throws Exception {
        assertTrue(widow.hasTwoCards());
    }

    @Test
    public void testHasTwoCards_Negative() throws Exception {
        widow.setCard1(null);
        assertFalse(widow.hasTwoCards());
    }

    @Test
    public void testRemove() throws Exception {
        assertTrue(widow.remove(Card.SPADE_ACE));
        assertThat(widow.getCard1(), nullValue());
        assertTrue(widow.remove(Card.CLUB_ACE));
        assertThat(widow.getCard2(), nullValue());
    }

    @Test
    public void testRemove_NoSuchCard() throws Exception {
        assertFalse(widow.remove(Card.DIAMOND_10));
    }

    @Test
    public void testAdd() throws Exception {
        Widow newWidow = new Widow();
        assertTrue(newWidow.add(Card.SPADE_ACE));
        assertTrue(newWidow.add(Card.CLUB_ACE));
        assertFalse(newWidow.add(Card.DIAMOND_10));
        assertThat(newWidow, equalTo(widow));
    }

    @Test
    public void testContainsCard() throws Exception {
        assertTrue(widow.containsCard(Card.SPADE_ACE));
        assertTrue(widow.containsCard(Card.CLUB_ACE));
        assertFalse(widow.containsCard(Card.DIAMOND_10));
        widow.setCard1(null);
        assertFalse(widow.containsCard(Card.SPADE_ACE));
    }
}
