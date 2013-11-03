package com.preferanser.client.application.widgets;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Image;
import com.preferanser.client.application.table.style.TurnPointerStyle;
import com.preferanser.shared.Cardinal;

public class TurnPointer extends Image {

    private final TurnPointerStyle style;
    private Cardinal turn;
    private boolean active;

    public TurnPointer(TurnPointerStyle style, ImageResource imageResource) {
        this.style = style;
        setResource(imageResource);
        setStylePrimaryName(style.turnPointer());
    }

    public void setTurn(Cardinal turn) {
        this.turn = turn;
        switch (turn) {
            case NORTH:
                addStyleName(style.turnPointerNorth());
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
    }

    public Cardinal getTurn() {
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
