package com.preferanser.client.application.widgets;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Element;

public class CardAnimation extends Animation {

    private final Element element;
    private int startX;
    private int startY;
    private int finalX;
    private int finalY;

    public CardAnimation(Element element) {
        this.element = element;
    }

    public void moveTo(int x, int y, int milliseconds) {
        finalX = x;
        finalY = y;

        startX = element.getOffsetLeft();
        startY = element.getOffsetTop();

        run(milliseconds);
    }

    @Override
    protected void onUpdate(double progress) {
        double positionX = startX + (progress * (this.finalX - startX));
        double positionY = startY + (progress * (this.finalY - startY));
        Style style = this.element.getStyle();
        style.setLeft(positionX, Style.Unit.PX);
        style.setTop(positionY, Style.Unit.PX);
    }

    @Override
    protected void onComplete() {
        super.onComplete();
        this.element.getStyle().setLeft(this.finalX, Style.Unit.PX);
        this.element.getStyle().setTop(this.finalY, Style.Unit.PX);
    }

}
