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

}
