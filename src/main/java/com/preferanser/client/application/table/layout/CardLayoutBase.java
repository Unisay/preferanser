package com.preferanser.client.application.table.layout;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.Widget;
import com.preferanser.client.application.table.CardView;

public abstract class CardLayoutBase implements CardLayout {

    final int startX;
    final int startY;
    final int startZ;

    protected CardLayoutBase(int startX, int startY, int startZ) {
        this.startX = startX;
        this.startY = startY;
        this.startZ = startZ;
    }

    @Override
    public void apply(Iterable<CardView> cardViews) {
        int x = startX, y = startY, z = startZ;
        CardView prev = null;
        for (CardView next : cardViews) {
            positionWidget(next.image, x, y, z);
            x += calculateOffsetX(prev, next);
            y += calculateOffsetY(prev, next);
            z += calculateOffsetZ(prev, next);
            prev = next;
        }
    }

    protected abstract int calculateOffsetX(CardView prev, CardView next);

    protected abstract int calculateOffsetY(CardView prev, CardView next);

    @SuppressWarnings("unused")
    protected int calculateOffsetZ(CardView prev, CardView next) {
        return 1;
    }

    private void positionWidget(Widget image, int x, int y, int z) {
        Style style = image.getElement().getStyle();
        style.setLeft(x, Style.Unit.PX);
        style.setTop(y, Style.Unit.PX);
        style.setZIndex(z);
    }

}
