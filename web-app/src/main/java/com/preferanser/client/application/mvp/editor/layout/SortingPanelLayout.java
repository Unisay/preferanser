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

package com.preferanser.client.application.mvp.editor.layout;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.gwt.user.client.ui.Panel;
import com.preferanser.client.application.widgets.CardWidget;
import com.preferanser.shared.domain.Card;
import com.preferanser.shared.domain.Rank;
import com.preferanser.shared.domain.Suit;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import static java.util.Collections.sort;

public abstract class SortingPanelLayout<T extends CardWidget> extends PanelLayout<T> {

    protected SortingPanelLayout(Panel panel) {
        super(panel);
    }

    @Override
    public final void layoutPanelWidgets(Collection<T> cardWidgets) {
        if (cardWidgets == null || cardWidgets.isEmpty())
            return;

        List<T> views = Lists.newArrayList(cardWidgets);
        sortCards(views);
        positionWidgets(views);
    }

    protected void sortCards(List<T> views) {
        sort(views, new Comparator<T>() {
            @Override
            public int compare(T cardWidget1, T cardWidget2) {
                return compareCards(cardWidget1.getCard(), cardWidget2.getCard());
            }
        });
    }

    protected int compareCards(Card card1, Card card2) {
        int suitResult = compareSuits(card1.getSuit(), card2.getSuit());
        if (0 == suitResult) {
            return compareRanks(card2.getRank(), card1.getRank());
        } else {
            return suitResult;
        }
    }

    protected int compareRanks(Rank rank1, Rank rank2) {
        return rank1.compareTo(rank2);
    }

    protected int compareSuits(Suit suit1, Suit suit2) {
        return suit1.compareTo(suit2);
    }

    protected void positionWidgets(List<T> cardWidgets) {
        Integer x = null, y = null, z = null;
        CardWidget prev = null;
        for (CardWidget next : cardWidgets) {
            x = getOffsetX(prev, next, x);
            y = getOffsetY(prev, next, y);
            z = getOffsetZ(prev, next, z);
            positionWidget(next, x, y, z);
            prev = next;
        }
    }

    protected int getStartZ() {
        return 0;
    }

    @SuppressWarnings("unused")
    protected int getOffsetX(CardWidget prev, CardWidget next, Integer prevX) {
        return prev == null ? getStartX() : prevX + getDeltaX(prev, next);
    }

    @SuppressWarnings("unused")
    protected int getDeltaX(CardWidget prev, CardWidget next) {
        return 0;
    }

    @SuppressWarnings("unused")
    protected int getOffsetY(CardWidget prev, CardWidget next, Integer prevY) {
        return prev == null ? getStartY() : prevY + getDeltaY(prev, next);
    }

    @SuppressWarnings("unused")
    protected int getDeltaY(CardWidget prev, CardWidget next) {
        return 0;
    }

    @SuppressWarnings("unused")
    protected int getOffsetZ(CardWidget prev, CardWidget next, Integer prevZ) {
        return prev == null ? getStartZ() : prevZ + getDeltaZ(prev, next);
    }

    @SuppressWarnings("unused")
    protected int getDeltaZ(CardWidget prev, CardWidget next) {
        return 1;
    }

    protected class CardWidgetCardTransformer implements Function<CardWidget, Card> {
        @Nullable
        @Override
        public Card apply(@Nullable CardWidget input) {
            assert input != null;
            return input.getCard();
        }
    }
}
