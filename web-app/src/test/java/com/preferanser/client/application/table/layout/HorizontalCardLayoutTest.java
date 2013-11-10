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

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.FlowPanel;
import com.googlecode.gwt.test.GwtModule;
import com.googlecode.gwt.test.GwtTestWithMockito;
import com.preferanser.client.application.widgets.CardWidget;
import com.preferanser.domain.Card;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static com.preferanser.domain.Card.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

@GwtModule("com.preferanser.Preferanser")
public class HorizontalCardLayoutTest extends GwtTestWithMockito {

    private HorizontalLayout layout;
    private Collection<Card> cards;
    private CardWidget clubAce;
    private CardWidget spadeAce;
    private CardWidget clubTen;

    @Before
    public void setUp() throws Exception {
        cards = newArrayList(CLUB_ACE, CLUB_TEN, SPADE_ACE, HEART_TEN, HEART_ACE);
        clubAce = new CardWidget(CLUB_ACE);
        spadeAce = new CardWidget(SPADE_ACE);
        clubTen = new CardWidget(CLUB_TEN);

        FlowPanel panel = new FlowPanel();
        layout = new HorizontalLayout(panel, 40);
    }

    @Test
    public void testApply() throws Exception {
        List<CardWidget> cardWidgets = Arrays.asList(
                positionImage(CLUB_ACE, 0, 0, 0),
                positionImage(SPADE_KING, 0, 0, 0),
                positionImage(SPADE_ACE, 0, 0, 0)
        );

        layout.apply(cardWidgets);

        List<CardWidget> expectedCardWidgets = Arrays.asList(
                positionImage(CLUB_ACE, 0, 0, 0),
                positionImage(SPADE_KING, 10, 0, 1),
                positionImage(SPADE_ACE, 20, 0, 2)
        );

        assertThat(cardWidgets, equalTo(expectedCardWidgets));
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

    private CardWidget positionImage(Card card, int left, int top, int z) {
        CardWidget cardWidget = new CardWidget(card);
        Style style = cardWidget.getElement().getStyle();
        style.setLeft(left, Style.Unit.PX);
        style.setTop(top, Style.Unit.PX);
        style.setZIndex(z);
        return cardWidget;
    }

}