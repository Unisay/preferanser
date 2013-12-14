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

import com.google.common.base.Preconditions;
import com.google.gwt.user.client.ui.Panel;
import com.preferanser.client.application.widgets.CardinalCard;

import java.util.Collection;

public class CenterLayout extends PanelLayout<CardinalCard> {

    private final int imageWidth;
    private final int imageHeight;

    public CenterLayout(Panel panel, int imageWidth, int imageHeight) {
        super(panel);
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
    }

    @Override
    public void apply(Collection<CardinalCard> cardinalCards) {
        Preconditions.checkArgument(cardinalCards.size() < 5, "Not more than 4 cards can be placed in center!");
        int z = 0;
        for (CardinalCard cardinalCard : cardinalCards) {
            positionCardWidget(cardinalCard, z++);
        }
    }

    private void positionCardWidget(CardinalCard cardinalCard, int z) {
        int x, y;
        switch (cardinalCard.getCardinal()) {
            case NORTH:
                x = (getWidth() - imageWidth) / 2;
                y = (getHeight() - imageHeight - imageHeight) / 2;
                positionWidget(cardinalCard.getCardWidget(), x, y, z);
                break;
            case EAST:
                x = getWidth() / 2;
                y = (getHeight() - imageHeight) / 2;
                positionWidget(cardinalCard.getCardWidget(), x, y, z);
                break;
            case SOUTH:
                x = (getWidth() - imageWidth) / 2;
                y = getHeight() / 2;
                positionWidget(cardinalCard.getCardWidget(), x, y, z);
                break;
            case WEST:
                x = (getWidth() - imageWidth - imageWidth) / 2;
                y = (getHeight() - imageHeight) / 2;
                positionWidget(cardinalCard.getCardWidget(), x, y, z);
                break;
            default:
                throw new IllegalStateException("Unknown Cardinal constant: " + cardinalCard.getCardinal());
        }
    }

}
