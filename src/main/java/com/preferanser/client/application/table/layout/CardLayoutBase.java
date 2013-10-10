package com.preferanser.client.application.table.layout;

import com.google.common.collect.Lists;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.Widget;
import com.preferanser.client.application.table.CardView;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import static java.util.Collections.sort;

public abstract class CardLayoutBase implements CardLayout {

    @Override
    public void apply(Collection<CardView> cardViews) {
        List<CardView> views = Lists.newArrayList(cardViews);

        sort(views, new Comparator<CardView>() {
            @Override public int compare(CardView view1, CardView view2) {
                int suitResult = view1.card.getSuit().compareTo(view2.card.getSuit());
                return 0 == suitResult ? view2.card.getRank().compareTo(view1.card.getRank()) : suitResult;
            }
        });

        int x = getStartX(), y = getStartY(), z = getStartZ();
        CardView prev = null;
        for (CardView next : views) {
            x += calculateOffsetX(prev, next);
            y += calculateOffsetY(prev, next);
            z += calculateOffsetZ(prev, next);
            positionWidget(next.image, x, y, z);
            prev = next;
        }
    }

    protected abstract int getStartX();

    protected abstract int getStartY();

    protected int getStartZ() {
        return 0;
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
