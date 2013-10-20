package com.preferanser.client.application.table.layout;

import com.google.common.base.Preconditions;
import com.google.gwt.user.client.ui.Panel;
import com.preferanser.client.application.table.CardView;
import com.preferanser.client.util.EnumRotator;
import com.preferanser.shared.Turn;

import java.util.Collection;

public class CenterCardLayout extends PanelCardLayout {

    private final int imageWidth;
    private final int imageHeight;
    private Turn firstTurn;

    public CenterCardLayout(Panel panel, int imageWidth, int imageHeight) {
        super(panel);
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
    }

    @Override
    public void apply(Collection<CardView> cardViews) {
        Preconditions.checkArgument(cardViews.size() < 5, "Not more than 4 cards can be placed in center!");
        EnumRotator<Turn> rotator = new EnumRotator<Turn>(Turn.values(), firstTurn);
        for (CardView cardView : cardViews) {
            positionCardView(cardView, rotator.next());
        }
    }

    private void positionCardView(CardView cardView, Turn turn) {
        int x, y;
        switch (turn) {
            case NORTH:
                x = (getDisposableWidth() - imageWidth) / 2;
                y = (getDisposableHeight() - imageHeight - imageHeight) / 2;
                positionWidget(cardView.image, x, y, 0);
                break;
            case EAST:
                x = getDisposableWidth() / 2;
                y = (getDisposableHeight() - imageHeight) / 2;
                positionWidget(cardView.image, x, y, 1);
                break;
            case SOUTH:
                x = (getDisposableWidth() - imageWidth) / 2;
                y = getDisposableHeight() / 2;
                positionWidget(cardView.image, x, y, 3);
                break;
            case WEST:
                x = (getDisposableWidth() - imageWidth - imageWidth) / 2;
                y = (getDisposableHeight() - imageHeight) / 2;
                positionWidget(cardView.image, x, y, 2);
                break;
            default:
                throw new IllegalStateException("Unknown Turn constant: " + turn);
        }
    }

    public void setFirstTurn(Turn firstTurn) {
        this.firstTurn = firstTurn;
    }

}
