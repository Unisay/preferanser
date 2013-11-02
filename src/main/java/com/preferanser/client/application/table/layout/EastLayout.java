package com.preferanser.client.application.table.layout;

import com.google.gwt.user.client.ui.Panel;
import com.preferanser.client.application.widgets.CardWidget;
import com.preferanser.shared.Card;
import com.preferanser.shared.Rank;

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
