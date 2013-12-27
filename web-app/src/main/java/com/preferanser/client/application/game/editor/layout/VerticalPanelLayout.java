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

package com.preferanser.client.application.game.editor.layout;

import com.google.gwt.user.client.ui.Panel;
import com.preferanser.client.application.widgets.CardWidget;
import com.preferanser.shared.domain.Card;
import com.preferanser.shared.domain.Suit;

import java.util.*;

import static com.google.common.collect.Collections2.transform;

public class VerticalPanelLayout<T extends CardWidget> extends SortingPanelLayout<T> {

    protected final int imageWidth;
    protected final int imageHeight;
    protected int sameSuitOffsetX;
    protected int diffSuitOffsetY;
    protected int minSameSuitOffsetX;
    protected int maxSameSuitOffsetX;
    protected int minDiffSuitOffsetY;
    protected int maxDiffSuitOffsetY;

    public VerticalPanelLayout(Panel panel, int imageWidth, int imageHeight) {
        super(panel);
        this.minSameSuitOffsetX = 24;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
    }

    @Override
    protected void positionWidgets(List<T> cardWidgets) {
        Collection<Card> cards = transform(cardWidgets, new CardWidgetCardTransformer());
        sameSuitOffsetX = calculateSameSuitOffsetX(cards);
        diffSuitOffsetY = calculateDiffSuitOffsetY(cards);
        super.positionWidgets(cardWidgets);
    }

    @Override
    protected int getStartY() {
        return 3 * PADDING;
    }

    @Override
    protected int getOffsetX(CardWidget prev, CardWidget next, Integer prevX) {
        if (prev == null || prev.getCard().getSuit() != next.getCard().getSuit()) {
            return getStartX();
        }
        return super.getOffsetX(prev, next, prevX);
    }

    @Override
    protected int getDeltaX(CardWidget prev, CardWidget next) {
        return sameSuitOffsetX;
    }

    @Override
    protected int getDeltaY(CardWidget prev, CardWidget next) {
        if (prev.getCard().getSuit() != next.getCard().getSuit()) {
            return diffSuitOffsetY;
        }
        return super.getDeltaY(prev, next);
    }

    protected int calculateSameSuitOffsetX(Collection<Card> cards) {
        int dx = minSameSuitOffsetX;
        int maxCardsSameSuit = getMaxCardsSameSuit(cards);
        int maxCardsOffsetCount = maxCardsSameSuit - 1;

        int minOccupiedWidth = maxCardsOffsetCount * minSameSuitOffsetX + imageWidth;
        int extraWidth = getDisposableWidth() - minOccupiedWidth;

        assert extraWidth >= 0 : "Cards are wider than panel";

        if (extraWidth > 0 && maxCardsOffsetCount > 0) {
            dx += extraWidth / maxCardsOffsetCount;
        }

        if (dx > maxSameSuitOffsetX) {
            dx = maxSameSuitOffsetX;
        }

        return dx;
    }

    protected int calculateDiffSuitOffsetY(Collection<Card> cards) {
        int dy = minDiffSuitOffsetY;

        int suitCount = getSuitCount(cards);
        assert suitCount > 0 : "Suit count < 0";
        int suitOffsetCount = suitCount - 1;

        int minOccupiedHeight = suitOffsetCount * minDiffSuitOffsetY + imageHeight;
        int extraHeight = getDisposableHeight() - minOccupiedHeight;

        assert extraHeight >= 0 : "Cards are taller than panel";

        if (extraHeight > 0 && suitOffsetCount > 0) {
            dy += extraHeight / suitOffsetCount;
        }

        if (dy > maxDiffSuitOffsetY) {
            dy = maxDiffSuitOffsetY;
        }

        return dy;
    }

    private int getSuitCount(Collection<Card> cards) {
        Set<Suit> suits = EnumSet.noneOf(Suit.class);
        for (Card card : cards) {
            suits.add(card.getSuit());
        }
        return suits.size();
    }

    private int getMaxCardsSameSuit(Collection<Card> cards) {
        Map<Suit, Integer> countMap = new EnumMap<Suit, Integer>(Suit.class);
        for (Suit suit : Suit.values()) {
            countMap.put(suit, 0);
        }
        for (Card card : cards) {
            countMap.put(card.getSuit(), countMap.get(card.getSuit()) + 1);
        }
        int max = 0;
        for (Integer count : countMap.values()) {
            if (max < count) {
                max = count;
            }
        }
        return max;
    }

}
