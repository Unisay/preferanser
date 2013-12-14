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
import com.preferanser.domain.Card;
import com.preferanser.domain.Rank;

public class EastLayout extends VerticalPanelLayout<CardWidget> {

    public EastLayout(Panel panel, int imageWidth, int imageHeight) {
        super(panel, imageWidth, imageHeight);
        this.maxSameSuitOffsetX = imageWidth - minSameSuitOffsetX;
        this.minDiffSuitOffsetY = 85;
        this.maxDiffSuitOffsetY = imageHeight;
    }

    @Override protected int compareRanks(Rank rank1, Rank rank2) {
        return -super.compareRanks(rank1, rank2);
    }

    @Override
    protected int getStartX() {
        return getWidth() - super.getStartX() - imageWidth;
    }

    @Override
    protected int getDeltaX(CardWidget prev, CardWidget next) {
        return -super.getDeltaX(prev, next);
    }

    @Override protected int getStartZ() {
        return Card.values().length;
    }

    @Override
    protected int getDeltaZ(CardWidget prev, CardWidget next) {
        if (prev.getCard().getSuit() == next.getCard().getSuit()) {
            return -1;
        } else {
            return 10;
        }
    }
}
