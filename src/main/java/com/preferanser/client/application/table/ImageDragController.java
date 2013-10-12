package com.preferanser.client.application.table;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.touch.client.Point;
import com.google.gwt.user.client.ui.Image;

public class ImageDragController {

    public Image image;
    public Point offsetPoint;

    public ImageDragController(Image image, MouseEvent event) {
        this.image = image;
        this.offsetPoint = new Point(event.getX(), event.getY());
    }

    public void updateImagePosition(MouseEvent event) {
        Document doc = Document.get();
        Point scrollPoint = new Point(doc.getScrollLeft(), doc.getScrollTop());
        Point eventPoint = new Point(event.getClientX(), event.getClientY());
        updateImagePosition(eventPoint.plus(scrollPoint).minus(offsetPoint));
    }

    private void updateImagePosition(Point point) {
        Style style = image.getElement().getStyle();
        style.setLeft(point.getX(), Style.Unit.PX);
        style.setTop(point.getY(), Style.Unit.PX);
    }
}
