package com.preferanser.client.application.table.layout;

import com.google.gwt.user.client.ui.Panel;
import com.preferanser.client.application.table.CardView;
import com.preferanser.shared.Card;
import com.preferanser.shared.Rank;

public class EastCardLayout extends VerticalPanelCardLayout {

    public EastCardLayout(Panel panel, int imageWidth, int imageHeight) {
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
    protected int getDeltaX(CardView prev, CardView next) {
        return -super.getDeltaX(prev, next);
    }

    @Override protected int getStartZ() {
        return Card.values().length;
    }

    @Override
    protected int getDeltaZ(CardView prev, CardView next) {
        if (prev.card.getSuit() == next.card.getSuit()) {
            return -1;
        } else {
            return 10;
        }
    }
}
