package com.preferanser.client.application.table.layout;

import com.google.common.base.Preconditions;
import com.google.gwt.user.client.ui.Panel;
import com.preferanser.client.application.widgets.CardWidget;
import com.preferanser.client.util.EnumRotator;
import com.preferanser.shared.Cardinal;

import java.util.Collection;

public class CenterCardLayout extends PanelCardLayout {

    private final int imageWidth;
    private final int imageHeight;
    private Cardinal firstTurn;

    public CenterCardLayout(Panel panel, int imageWidth, int imageHeight) {
        super(panel);
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
    }

    @Override
    public void apply(Collection<CardWidget> cardWidgets) {
        Preconditions.checkArgument(cardWidgets.size() < 5, "Not more than 4 cards can be placed in center!");
        EnumRotator<Cardinal> rotator = new EnumRotator<Cardinal>(Cardinal.values(), firstTurn);
        for (CardWidget cardWidget : cardWidgets) {
            positionCardWidget(cardWidget, rotator.next());
        }
    }

    private void positionCardWidget(CardWidget cardWidget, Cardinal cardinal) {
        int x, y;
        switch (cardinal) {
            case NORTH:
                x = (getDisposableWidth() - imageWidth) / 2;
                y = (getDisposableHeight() - imageHeight - imageHeight) / 2;
                positionWidget(cardWidget, x, y, 0);
                break;
            case EAST:
                x = getDisposableWidth() / 2;
                y = (getDisposableHeight() - imageHeight) / 2;
                positionWidget(cardWidget, x, y, 1);
                break;
            case SOUTH:
                x = (getDisposableWidth() - imageWidth) / 2;
                y = getDisposableHeight() / 2;
                positionWidget(cardWidget, x, y, 3);
                break;
            case WEST:
                x = (getDisposableWidth() - imageWidth - imageWidth) / 2;
                y = (getDisposableHeight() - imageHeight) / 2;
                positionWidget(cardWidget, x, y, 2);
                break;
            default:
                throw new IllegalStateException("Unknown Cardinal constant: " + cardinal);
        }
    }

    public void setFirstTurn(Cardinal firstTurn) {
        this.firstTurn = firstTurn;
    }

}
