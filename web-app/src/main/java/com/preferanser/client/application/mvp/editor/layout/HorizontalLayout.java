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

import com.google.gwt.user.client.ui.Panel;
import com.preferanser.client.application.widgets.CardWidget;
import com.preferanser.shared.domain.Card;

import java.util.Collection;
import java.util.List;

import static com.google.common.collect.Collections2.transform;

public class HorizontalLayout extends SortingPanelLayout<CardWidget> {

    private final int imageWidth;
    private final int minOffsetDiffSuit;
    private final int maxOffsetDiffSuit;
    private final int minOffsetSameSuit;
    private final int maxOffsetSameSuit;
    private int sameSuitOffsetX;
    private int diffSuitOffsetX;
    private int sameSuitOffsetCount;
    private int diffSuitOffsetCount;

    public HorizontalLayout(Panel panel, int imageWidth) {
        super(panel);
        this.imageWidth = imageWidth;
        this.minOffsetSameSuit = 24;
        this.maxOffsetSameSuit = 2 * minOffsetSameSuit;
        this.minOffsetDiffSuit = imageWidth;
        this.maxOffsetDiffSuit = imageWidth + imageWidth / 2;
    }

    @Override
    protected void positionWidgets(List<CardWidget> cardWidgets) {
        Collection<Card> cards = transform(cardWidgets, new CardWidgetCardTransformer());
        diffSuitOffsetCount = countDiffSuitOffsets(cards);
        sameSuitOffsetCount = countSameSuitOffsets(cards);
        diffSuitOffsetX = calculateDiffSuitOffsetX();
        sameSuitOffsetX = calculateSameSuitOffsetX();
        super.positionWidgets(cardWidgets);
    }

    @Override
    protected int getStartX() {
        return (getWidth() - getCardsWidth()) / 2;
    }

    @Override
    protected int getStartY() {
        return PADDING;
    }

    @Override
    protected int getDeltaX(CardWidget prev, CardWidget next) {
        return prev.getCard().getSuit() == next.getCard().getSuit()
            ? sameSuitOffsetX
            : diffSuitOffsetX;
    }

    private int getCardsWidth() {
        return sameSuitOffsetCount * sameSuitOffsetX + diffSuitOffsetCount * diffSuitOffsetX + imageWidth;
    }

    private int calculateDiffSuitOffsetX() {
        int dx;

        int availableSpace = getDisposableWidth() - getSameSuitWidth();
        if (availableSpace > 0) {
            // Cards are narrower than panel
            dx = diffSuitOffsetCount == 0 ? maxOffsetDiffSuit : availableSpace / diffSuitOffsetCount;
        } else {
            // Cards are wider than panel
            dx = maxOffsetDiffSuit - availableSpace / diffSuitOffsetCount;
        }

        if (dx > maxOffsetDiffSuit) {
            dx = maxOffsetDiffSuit;
        }
        if (dx < minOffsetDiffSuit) {
            dx = minOffsetDiffSuit;
        }
        return dx;
    }

    private int calculateSameSuitOffsetX() {
        int dx = minOffsetSameSuit;
        int occupiedWidth = getSameSuitWidth() + diffSuitOffsetCount * diffSuitOffsetX;
        int availableSpace = getDisposableWidth() - occupiedWidth;

        if (availableSpace > 0) {
            dx += sameSuitOffsetCount == 0 ? maxOffsetSameSuit : availableSpace / sameSuitOffsetCount;
        }

        if (dx < minOffsetSameSuit) {
            dx = minOffsetSameSuit;
        }
        if (dx > maxOffsetSameSuit) {
            dx = maxOffsetSameSuit;
        }
        return dx;
    }

    private int getSameSuitWidth() {
        return sameSuitOffsetCount * minOffsetSameSuit + imageWidth;
    }

    protected static int countSameSuitOffsets(Collection<Card> cards) {
        int count = 0;
        Card prev = null;
        for (Card card : cards) {
            if (prev != null && prev.getSuit() == card.getSuit()) {
                count++;
            }
            prev = card;
        }
        return count;
    }

    protected static int countDiffSuitOffsets(Collection<Card> cards) {
        int count = 0;
        Card prev = null;
        for (Card card : cards) {
            if (prev != null && prev.getSuit() != card.getSuit()) {
                count++;
            }
            prev = card;
        }
        return count;
    }

}
