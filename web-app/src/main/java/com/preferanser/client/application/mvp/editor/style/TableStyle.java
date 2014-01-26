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

package com.preferanser.client.application.mvp.editor.style;

import com.google.gwt.resources.client.CssResource;

/**
 * Css resource for editor styles
 */
public interface TableStyle extends CssResource, TurnPointerStyle {

    @ClassName("gwt-Label") String gwtLabel();

    @ClassName("not-visible") String notVisible();

    String trickCountWidow();

    String trickCountWest();

    String titleEast();

    String trickCountEast();

    String trickCountSouth();

    @ClassName("not-displayed") String notDisplayed();

    String contractTricks();

    String contractSuit();

    String noTrump();

    String contractLabel();

    String contractLink();

}