package com.preferanser.client.application.widgets;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Element;

import static com.google.gwt.dom.client.Style.Unit.PX;

public class CardAnimation extends Animation {

    private static final double MIN_PROGRESS_DELTA = 0.05;
    private final Element element;
    private int startX;
    private int startY;
    private int finalX;
    private int finalY;
    private double lastProgress = 0;

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
        double progressDelta = progress - lastProgress;
        if (progressDelta < MIN_PROGRESS_DELTA)
            return;

        double positionX = startX + (progress * (finalX - startX));
        double positionY = startY + (progress * (finalY - startY));

        Style style = element.getStyle();
        style.setLeft(positionX, PX);
        style.setTop(positionY, PX);

        lastProgress = progress;
    }

    @Override
    protected void onComplete() {
        super.onComplete();
        element.getStyle().setLeft(finalX, PX);
        element.getStyle().setTop(finalY, PX);
    }

}
