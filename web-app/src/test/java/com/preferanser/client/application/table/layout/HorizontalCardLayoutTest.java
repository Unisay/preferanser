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

package com.preferanser.client.application.table.layout;

import com.preferanser.client.application.widgets.CardWidget;
import com.preferanser.domain.Card;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;

import static com.google.common.collect.Lists.newArrayList;
import static com.preferanser.domain.Card.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class HorizontalCardLayoutTest {

    private HorizontalLayout layout;
    private Collection<Card> cards;
    private CardWidget clubAce;
    private CardWidget spadeAce;
    private CardWidget clubTen;

    @Before
    public void setUp() throws Exception {
        layout = new HorizontalLayout(null, 30);
        cards = newArrayList(CLUB_ACE, CLUB_TEN, SPADE_ACE, HEART_TEN, HEART_ACE);
        clubAce = new CardWidget(CLUB_ACE);
        spadeAce = new CardWidget(SPADE_ACE);
        clubTen = new CardWidget(CLUB_TEN);
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
        assertThat(HorizontalLayout.countSameSuitOffsets(cards), equalTo(2));
    }

    @Test
    public void testCountDiffSuitOffsets() throws Exception {
        assertThat(HorizontalLayout.countDiffSuitOffsets(cards), equalTo(2));
    }

}
