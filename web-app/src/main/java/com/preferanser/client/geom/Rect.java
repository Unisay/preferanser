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

package com.preferanser.client.geom;

import com.google.gwt.user.client.ui.Widget;

public class Rect {

    private Point leftTop;
    private Point rightBottom;

    public Rect(Point leftTop, Point rightBottom) {
        this.leftTop = leftTop;
        this.rightBottom = rightBottom;
    }

    public static Rect FromWidget(Widget widget) {
        return new Rect(Point.FromWidgetLeftTop(widget), Point.FromWidgetRightBottom(widget));
    }

    /**
     * TODO: unit test
     */
    public boolean contains(Point point) {
        return leftTop.getX() <= point.getX() && leftTop.getY() <= point.getY() &&
                rightBottom.getX() >= point.getX() && rightBottom.getY() >= point.getY();
    }

    /**
     * TODO: unit test
     */
    public Point center() {
        return leftTop.plus(rightBottom.minus(leftTop).divide(2));
    }
}
