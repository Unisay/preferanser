/*
 * Preferanser is a program to simulate and calculate Russian Preferans Card game deals.
 *
 *     Copyright (C) 2013  Yuriy Lazarev <Yuriy.Lazarev@gmail.com>
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see [http://www.gnu.org/licenses/].
 */

package com.preferanser.laf.client;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.cellview.client.DataGrid;

/**
 * GWT Resources
 * <p/>
 * <a href="http://www.gwtproject.org/doc/latest/DevGuideClientBundle.html">ClientBundle</a>
 */
public interface PreferanserResources extends ClientBundle {

    @SuppressWarnings("unused")
    interface Style extends CssResource {

        String title();

        @ClassName("title-smaller") String titleSmaller();

        String dialogTop();

        String Caption();

        String layoutCenter();

        @ClassName("gwt-DialogBox") String gwtDialogBox();

        @ClassName("gwt-DialogBoxOverlay") String gwtDialogBoxOverlay();

        @ClassName("gwt-Button") String gwtButton();

        @ClassName("gwt-TabBarFirst") String gwtTabBarFirst();

        @ClassName("gwt-TabBarRest") String gwtTabBarRest();

        @ClassName("gwt-Label") String gwtLabel();

        @ClassName("gwt-TabBarItem") String gwtTabBarItem();

        @ClassName("gwt-TabBar") String gwtTabBar();

        @ClassName("gwt-TabPanelBottom") String gwtTabPanelBottom();

        @ClassName("gwt-TabBarItem-selected") String gwtTabBarItemSelected();

    }

    interface DataGridResources extends DataGrid.Resources {

        interface DataGridStyle extends DataGrid.Style {}

        @Override
        @Source({DataGrid.Style.DEFAULT_CSS, "DataGrid.css"}) DataGridStyle dataGridStyle();
    }

    @Source("style.css") Style css();

    DataGridResources dataGrid();

    @Source("images/arrow_right.png")
    @ImageResource.ImageOptions(height = 32, width = 32) ImageResource arrowRight();

    @Source("images/green_cloth_back.jpg")
    @ImageResource.ImageOptions(repeatStyle = ImageResource.RepeatStyle.Horizontal) ImageResource pageBackground();

    @Source("images/wood_back.png")
    @ImageResource.ImageOptions(repeatStyle = ImageResource.RepeatStyle.Both) ImageResource dialogBackground();

    @Source("images/cards/c7.png")
    @ImageResource.ImageOptions(width = 109, height = 144) ImageResource c7();

    @Source("images/cards/s7.png")
    @ImageResource.ImageOptions(width = 109, height = 144) ImageResource s7();

    @Source("images/cards/d7.png")
    @ImageResource.ImageOptions(width = 109, height = 144) ImageResource d7();

    @Source("images/cards/h7.png")
    @ImageResource.ImageOptions(width = 109, height = 144) ImageResource h7();

    @Source("images/cards/c8.png")
    @ImageResource.ImageOptions(width = 109, height = 144) ImageResource c8();

    @Source("images/cards/s8.png")
    @ImageResource.ImageOptions(width = 109, height = 144) ImageResource s8();

    @Source("images/cards/d8.png")
    @ImageResource.ImageOptions(width = 109, height = 144) ImageResource d8();

    @Source("images/cards/h8.png")
    @ImageResource.ImageOptions(width = 109, height = 144) ImageResource h8();

    @Source("images/cards/c9.png")
    @ImageResource.ImageOptions(width = 109, height = 144) ImageResource c9();

    @Source("images/cards/s9.png")
    @ImageResource.ImageOptions(width = 109, height = 144) ImageResource s9();

    @Source("images/cards/d9.png")
    @ImageResource.ImageOptions(width = 109, height = 144) ImageResource d9();

    @Source("images/cards/h9.png")
    @ImageResource.ImageOptions(width = 109, height = 144) ImageResource h9();

    @Source("images/cards/c10.png")
    @ImageResource.ImageOptions(width = 109, height = 144) ImageResource c10();

    @Source("images/cards/s10.png")
    @ImageResource.ImageOptions(width = 109, height = 144) ImageResource s10();

    @Source("images/cards/d10.png")
    @ImageResource.ImageOptions(width = 109, height = 144) ImageResource d10();

    @Source("images/cards/h10.png")
    @ImageResource.ImageOptions(width = 109, height = 144) ImageResource h10();

    @Source("images/cards/cj.png")
    @ImageResource.ImageOptions(width = 109, height = 144) ImageResource cj();

    @Source("images/cards/sj.png")
    @ImageResource.ImageOptions(width = 109, height = 144) ImageResource sj();

    @Source("images/cards/dj.png")
    @ImageResource.ImageOptions(width = 109, height = 144) ImageResource dj();

    @Source("images/cards/hj.png")
    @ImageResource.ImageOptions(width = 109, height = 144) ImageResource hj();

    @Source("images/cards/cq.png")
    @ImageResource.ImageOptions(width = 109, height = 144) ImageResource cq();

    @Source("images/cards/sq.png")
    @ImageResource.ImageOptions(width = 109, height = 144) ImageResource sq();

    @Source("images/cards/dq.png")
    @ImageResource.ImageOptions(width = 109, height = 144) ImageResource dq();

    @Source("images/cards/hq.png")
    @ImageResource.ImageOptions(width = 109, height = 144) ImageResource hq();

    @Source("images/cards/ck.png")
    @ImageResource.ImageOptions(width = 109, height = 144) ImageResource ck();

    @Source("images/cards/sk.png")
    @ImageResource.ImageOptions(width = 109, height = 144) ImageResource sk();

    @Source("images/cards/dk.png")
    @ImageResource.ImageOptions(width = 109, height = 144) ImageResource dk();

    @Source("images/cards/hk.png")
    @ImageResource.ImageOptions(width = 109, height = 144) ImageResource hk();

    @Source("images/cards/ca.png")
    @ImageResource.ImageOptions(width = 109, height = 144) ImageResource ca();

    @Source("images/cards/sa.png")
    @ImageResource.ImageOptions(width = 109, height = 144) ImageResource sa();

    @Source("images/cards/da.png")
    @ImageResource.ImageOptions(width = 109, height = 144) ImageResource da();

    @Source("images/cards/ha.png")
    @ImageResource.ImageOptions(width = 109, height = 144) ImageResource ha();

}
