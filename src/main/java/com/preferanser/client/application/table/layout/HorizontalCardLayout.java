package com.preferanser.client.application.table.layout;

import com.google.gwt.user.client.ui.Panel;
import com.preferanser.client.application.table.CardView;

public class HorizontalCardLayout extends PanelCardLayout {

    public HorizontalCardLayout(Panel panel) {
        super(panel);
    }

    @Override protected int calculateOffsetX(CardView prev, CardView next) {
        return 10;
    }

    @Override protected int calculateOffsetY(CardView prev, CardView next) {
        return 0;
    }

}
