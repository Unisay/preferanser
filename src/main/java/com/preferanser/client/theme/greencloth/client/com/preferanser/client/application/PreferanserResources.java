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

package com.preferanser.client.theme.greencloth.client.com.preferanser.client.application;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;

/**
 * GWT Resources
 * <p/>
 * <a href="http://www.gwtproject.org/doc/latest/DevGuideClientBundle.html">ClientBundle</a>
 */
public interface PreferanserResources extends ClientBundle {

    interface GreenClothCssResource extends CssResource {

        @ClassName("gwt-DialogBox") String gwtDialogBox();

        @ClassName("gwt-DialogBoxOverlay") String gwtDialogBoxOverlay();

        @SuppressWarnings("unused") String dialogTop();

        @SuppressWarnings("unused") String Caption();
    }

    @Source("greencloth.css") GreenClothCssResource css();

    @Source("images/table_background_classic.jpg")
    @ImageResource.ImageOptions(repeatStyle = ImageResource.RepeatStyle.Horizontal) ImageResource background();

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