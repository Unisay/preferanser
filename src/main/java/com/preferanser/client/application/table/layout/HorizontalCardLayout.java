package com.preferanser.client.application.table.layout;

import com.google.gwt.user.client.ui.Panel;
import com.preferanser.client.application.table.CardView;

public class HorizontalCardLayout extends PanelCardLayout {

    private static final int DX_SAME_SUIT = 24;
    private static final int DX_DIFF_SUIT = 115;

    public HorizontalCardLayout(Panel panel) {
        super(panel);
    }

    @Override
    protected int calculateOffsetX(CardView prev, CardView next) {
        if (prev == null) {
            return 0;
        }
        return prev.card.getSuit() == next.card.getSuit() ? DX_SAME_SUIT : DX_DIFF_SUIT;
    }

    @Override
    protected int calculateOffsetY(CardView prev, CardView next) {
        return 0;
    }

}
