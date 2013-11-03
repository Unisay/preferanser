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

package com.preferanser.client.application.table.style;

import com.google.gwt.resources.client.CssResource;

/**
 * Css resource for table styles
 */
public interface TableStyle extends CssResource, TurnPointerStyle {

    String card();

    String southDock();

    String titleWest();

    String titleNorth();

    String northVPanel();

    String handHeaderLeftPanel();

    String westHPanel();

    String northHPanel();

    String eastDock();

    @ClassName("gwt-Label") String gwtLabel();

    String centerVPanel();

    String dragging();

    String trickCountNorth();

    @ClassName("not-visible") String notVisible();

    String trickCountWest();

    String northDock();

    String verticalPanel();

    String westVPanel();

    String centerHPanel();

    String eastFlowPanel();

    String southFlowPanel();

    String southHPanel();

    String dockPanel();

    String northFlowPanel();

    String titleEast();

    String trickCountEast();

    String trickCountSouth();

    String eastVPanel();

    String titleSouth();

    String centerFlowPanel();

    String southVPanel();

    String westDock();

    String westFlowPanel();

    String handTitle();

    String centerDock();

    String eastHPanel();

    String buttonsPanel();

    @ClassName("not-displayed") String notDisplayed();

    String contractTricks();

    String contractSuit();

    String noTrump();

    String contractLabel();

    String contractLink();


}
