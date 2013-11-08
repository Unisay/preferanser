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

import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.user.client.ui.Widget;

public class Point {

    private final int x;
    private final int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public static Point FromMouseEvent(MouseEvent event) {
        return new Point(event.getClientX(), event.getClientY());
    }

    public static Point FromMouseEvent(MouseEvent event, Document doc) {
        return FromMouseEvent(event).plus(new Point(doc.getScrollLeft(), doc.getScrollTop()));
    }

    public static Point FromMouseEventRelative(MouseEvent event) {
        return new Point(event.getX(), event.getY());
    }

    public static Point FromWidgetLeftTop(Widget widget) {
        return new Point(widget.getAbsoluteLeft(), widget.getAbsoluteTop());
    }

    public static Point FromWidgetRightBottom(Widget widget) {
        return new Point(
                widget.getAbsoluteLeft() + widget.getOffsetWidth(),
                widget.getAbsoluteTop() + widget.getOffsetHeight());
    }

    public Point plus(Point that) {
        return new Point(this.x + that.x, this.y + that.y);
    }

    public Point minus(Point that) {
        return new Point(this.x - that.x, this.y - that.y);
    }

    public Point divide(int scalar) {
        return new Point(this.getX() / scalar, this.getY() / scalar);
    }

    public Point multiply(int scalar) {
        return new Point(this.getX() * scalar, this.getY() * scalar);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

}
