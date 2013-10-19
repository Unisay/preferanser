package com.preferanser.client.application.table;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.user.client.ui.Image;
import com.preferanser.client.geom.Point;

public class ImageDragController {

    private static final String STYLE_DRAGGING = "dragging";
    private Image image;
    private Point offsetPoint;
    private boolean drag = false;
    private final Document doc;

    public ImageDragController(Document doc) {
        this.doc = doc;
    }

    public void startDrag(Image image, MouseEvent event) {
        this.image = image;
        this.offsetPoint = new Point(event.getX(), event.getY());
        // noinspection GWTStyleCheck
        image.addStyleName(STYLE_DRAGGING);
        drag = true;
    }

    public void stopDrag() {
        // noinspection GWTStyleCheck
        image.removeStyleName(STYLE_DRAGGING);
        drag = false;
    }

    public void updateImagePosition(MouseEvent event) {
        updateImagePosition(Point.FromMouseEvent(event, doc).minus(offsetPoint));
    }

    private void updateImagePosition(Point point) {
        Style style = image.getElement().getStyle();
        style.setLeft(point.getX(), Style.Unit.PX);
        style.setTop(point.getY(), Style.Unit.PX);
    }

    public boolean isDrag() {
        return drag;
    }

    public Image getImage() {
        return image;
    }
}
