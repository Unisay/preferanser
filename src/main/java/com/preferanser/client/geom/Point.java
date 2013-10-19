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


    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
