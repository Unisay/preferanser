package com.preferanser.client.application.table.layout;

import com.google.gwt.user.client.ui.Panel;
import com.preferanser.client.application.table.CardView;

public class CenterCardLayout extends PanelCardLayout {

    public CenterCardLayout(Panel panel) {
        super(panel);
    }

    @Override protected int calculateOffsetX(CardView prev, CardView next) {
        return 0; //Todo
    }

    @Override protected int calculateOffsetY(CardView prev, CardView next) {
        return 0;  //Todo
    }
}