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
