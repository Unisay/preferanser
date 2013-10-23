package com.preferanser.client.application.table;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.MouseEvent;
import com.preferanser.client.application.widgets.CardWidget;
import com.preferanser.client.geom.Point;

public class ImageDragController {

    private static final String STYLE_DRAGGING = "dragging";
    private CardWidget cardWidget;
    private Point clickOffset;
    private Point parentOffset;
    private boolean drag = false;
    private final Document doc;

    public ImageDragController(Document doc) {
        this.doc = doc;
    }

    public void startDrag(CardWidget cardWidget, MouseEvent event) {
        this.cardWidget = cardWidget;
        this.parentOffset = Point.FromWidgetLeftTop(cardWidget.getParent());
        this.clickOffset = Point.FromMouseEventRelative(event);
        // noinspection GWTStyleCheck
        cardWidget.addStyleName(STYLE_DRAGGING);
        drag = true;
    }

    public void stopDrag() {
        // noinspection GWTStyleCheck
        cardWidget.removeStyleName(STYLE_DRAGGING);
        drag = false;
    }

    public void updateImagePosition(MouseEvent event) {
        updateImagePosition(Point.FromMouseEvent(event, doc).minus(clickOffset).minus(parentOffset));
    }

    private void updateImagePosition(Point point) {
        Style style = cardWidget.getElement().getStyle();
        style.setLeft(point.getX(), Style.Unit.PX);
        style.setTop(point.getY(), Style.Unit.PX);
    }

    public boolean isDrag() {
        return drag;
    }

    public CardWidget getCardWidget() {
        return cardWidget;
    }
}
