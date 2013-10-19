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
        sortCards(views);
        positionWidgets(views);
    }

    protected void sortCards(List<CardView> views) {
        sort(views, new Comparator<CardView>() {
            @Override public int compare(CardView view1, CardView view2) {
                int suitResult = view1.card.getSuit().compareTo(view2.card.getSuit());
                return 0 == suitResult ? view2.card.getRank().compareTo(view1.card.getRank()) : suitResult;
            }
        });
    }

    protected void positionWidgets(List<CardView> views) {
        int x = getStartX(), y = getStartY(), z = getStartZ();
        CardView prev = null;
        for (CardView next : views) {
            x += getOffsetX(prev, next);
            y += getOffsetY(prev, next);
            z += calculateOffsetZ(prev, next);
            positionWidget(next.image, x, y, z);
            prev = next;
        }
    }

    protected int getStartX() {
        return 0;
    }

    protected int getStartY() {
        return 0;
    }

    protected int getStartZ() {
        return 0;
    }

    protected int getOffsetX(CardView prev, CardView next) {
        return 0;
    }

    protected int getOffsetY(CardView prev, CardView next) {
        return 0;
    }

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
