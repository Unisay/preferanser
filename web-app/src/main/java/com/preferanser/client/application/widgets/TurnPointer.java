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

package com.preferanser.client.application.widgets;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Image;
import com.preferanser.client.application.mvp.editor.style.TurnPointerStyle;
import com.preferanser.shared.domain.Hand;

public class TurnPointer extends Image {

    private final TurnPointerStyle style;
    private Hand turn;
    private boolean active;

    public TurnPointer(TurnPointerStyle style, ImageResource imageResource) {
        this.style = style;
        setResource(imageResource);
        setStylePrimaryName(style.turnPointer());
    }

    public void setTurn(Hand turn) {
        this.turn = turn;
        switch (turn) {
            case WIDOW:
                addStyleName(style.turnPointerWidow());
                break;
            case EAST:
                addStyleName(style.turnPointerEast());
                break;
            case SOUTH:
                addStyleName(style.turnPointerSouth());
                break;
            case WEST:
                addStyleName(style.turnPointerWest());
                break;
        }
        ensureDebugId("turn-pointer-" + turn.name());
    }

    public Hand getTurn() {
        return turn;
    }

    public void setActive(boolean active) {
        this.active = active;
        if (active) {
            addStyleName(style.turnPointerActive());
        } else {
            removeStyleName(style.turnPointerActive());
        }
    }

    public boolean isActive() {
        return active;
    }
}
