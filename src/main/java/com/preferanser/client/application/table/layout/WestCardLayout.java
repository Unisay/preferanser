package com.preferanser.client.application.table.layout;

import com.google.gwt.user.client.ui.Panel;

public class WestCardLayout extends VerticalPanelCardLayout {

    public WestCardLayout(Panel panel, int imageWidth, int imageHeight) {
        super(panel, imageWidth, imageHeight);
        this.maxSameSuitOffsetX = imageWidth - minSameSuitOffsetX;
        this.minDiffSuitOffsetY = 85;
        this.maxDiffSuitOffsetY = imageHeight;
    }

}
