package com.preferanser.client.geom;

import com.google.gwt.user.client.ui.Widget;

public class Rect {

    private Point leftTop;
    private Point rightBottom;

    public Rect(Point leftTop, Point rightBottom) {
        this.leftTop = leftTop;
        this.rightBottom = rightBottom;
    }

    public Rect(int x1, int y1, int x2, int y2) {
        this(new Point(x1, y1), new Point(x2, y2));
    }

    public static Rect FromWidget(Widget widget) {
        int left = widget.getAbsoluteLeft();
        int top = widget.getAbsoluteTop();
        return new Rect(left, top, left + widget.getOffsetWidth(), top + widget.getOffsetHeight());
    }

    /**
     * TODO: unit test
     */
    public boolean contains(Point point) {
        return leftTop.getX() <= point.getX() && leftTop.getY() <= point.getY() &&
                rightBottom.getX() >= point.getX() && rightBottom.getY() >= point.getY();
    }

}
