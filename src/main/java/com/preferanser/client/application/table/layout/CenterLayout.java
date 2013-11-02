package com.preferanser.client.application.table.layout;

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
                x = (getDisposableWidth() - imageWidth) / 2;
                y = (getDisposableHeight() - imageHeight - imageHeight) / 2;
                positionWidget(cardinalCard.getCardWidget(), x, y, z);
                break;
            case EAST:
                x = getDisposableWidth() / 2;
                y = (getDisposableHeight() - imageHeight) / 2;
                positionWidget(cardinalCard.getCardWidget(), x, y, z);
                break;
            case SOUTH:
                x = (getDisposableWidth() - imageWidth) / 2;
                y = getDisposableHeight() / 2;
                positionWidget(cardinalCard.getCardWidget(), x, y, z);
                break;
            case WEST:
                x = (getDisposableWidth() - imageWidth - imageWidth) / 2;
                y = (getDisposableHeight() - imageHeight) / 2;
                positionWidget(cardinalCard.getCardWidget(), x, y, z);
                break;
            default:
                throw new IllegalStateException("Unknown Cardinal constant: " + cardinalCard.getCardinal());
        }
    }

}
