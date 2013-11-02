package com.preferanser.client.application.table.layout;

import com.google.gwt.user.client.ui.Panel;
import com.preferanser.client.application.widgets.CardWidget;

public class WestLayout extends VerticalPanelLayout<CardWidget> {

    public WestLayout(Panel panel, int imageWidth, int imageHeight) {
        super(panel, imageWidth, imageHeight);
        this.maxSameSuitOffsetX = imageWidth - minSameSuitOffsetX;
        this.minDiffSuitOffsetY = 85;
        this.maxDiffSuitOffsetY = imageHeight;
    }

}
