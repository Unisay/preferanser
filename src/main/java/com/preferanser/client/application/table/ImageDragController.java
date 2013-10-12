package com.preferanser.client.application.table;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.touch.client.Point;
import com.google.gwt.user.client.ui.Image;

public class ImageDragController {

    public Image image;
    public Point point;

    public ImageDragController(Image image, MouseEvent event) {
        this.image = image;
        this.point = new Point(event.getX(), event.getY());
    }

    public void updateImagePosition(MouseEvent event) {
        Point mousePoint = new Point(event.getClientX(), event.getClientY());
        updateImagePosition(mousePoint.minus(point));
    }

    private void updateImagePosition(Point point) {
        Style style = image.getElement().getStyle();
        style.setLeft(point.getX(), Style.Unit.PX);
        style.setTop(point.getY(), Style.Unit.PX);
    }
}
