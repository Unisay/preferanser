package com.preferanser.client.application.table.layout;

import com.preferanser.client.application.table.CardView;
import com.preferanser.shared.Card;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;

import static com.google.common.collect.Lists.newArrayList;
import static com.preferanser.shared.Card.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class HorizontalCardLayoutTest {

    private HorizontalCardLayout layout;
    private Collection<Card> cards;
    private CardView clubAce;
    private CardView spadeAce;
    private CardView clubTen;

    @Before
    public void setUp() throws Exception {
        layout = new HorizontalCardLayout(null, 30);
        cards = newArrayList(CLUB_ACE, CLUB_TEN, SPADE_ACE, HEART_TEN, HEART_ACE);
        clubAce = new CardView(CLUB_ACE, null);
        spadeAce = new CardView(SPADE_ACE, null);
        clubTen = new CardView(CLUB_TEN, null);
    }

    @Test
    public void testCalculateOffsetX() throws Exception {
        assertThat(layout.getOffsetX(clubAce, spadeAce, 10), equalTo(10));
        assertThat(layout.getOffsetX(clubAce, clubTen, 10), equalTo(10));
    }

    @Test
    public void testCalculateOffsetY() throws Exception {
        assertThat(layout.getOffsetY(clubAce, spadeAce, 10), equalTo(10));
        assertThat(layout.getOffsetY(clubAce, clubTen, 10), equalTo(10));
    }

    @Test
    public void testCountSameSuitOffsets() throws Exception {
        assertThat(HorizontalCardLayout.countSameSuitOffsets(cards), equalTo(2));
    }

    @Test
    public void testCountDiffSuitOffsets() throws Exception {
        assertThat(HorizontalCardLayout.countDiffSuitOffsets(cards), equalTo(2));
    }

}
