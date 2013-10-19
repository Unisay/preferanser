package com.preferanser.client.geom;

import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.MouseEvent;

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
