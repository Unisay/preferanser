package com.preferanser.client.application.table;

import com.google.gwt.user.client.ui.Panel;
import com.preferanser.client.application.table.layout.PanelCardLayout;

public class WestCardLayout extends PanelCardLayout {

    public WestCardLayout(Panel panel) {
        super(panel);
    }

    @Override protected int calculateOffsetX(CardView prev, CardView next) {
        return 0;  //Todo
    }

    @Override protected int calculateOffsetY(CardView prev, CardView next) {
        return 0;  //Todo
    }
}
