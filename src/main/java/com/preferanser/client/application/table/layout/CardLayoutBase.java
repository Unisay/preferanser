package com.preferanser.client.application.table.layout;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.Widget;
import com.preferanser.client.application.table.CardView;
import com.preferanser.shared.Card;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import static java.util.Collections.sort;

public abstract class CardLayoutBase implements CardLayout {

    @Override
    public void apply(Collection<CardView> cardViews) {
        if (cardViews == null || cardViews.isEmpty())
            return;

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
        Integer x = null, y = null, z = null;
        CardView prev = null;
        for (CardView next : views) {
            x = getOffsetX(prev, next, x);
            y = getOffsetY(prev, next, y);
            z = getOffsetZ(prev, next, z);
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

    @SuppressWarnings("unused")
    protected int getOffsetX(CardView prev, CardView next, Integer prevX) {
        return prev == null ? getStartX() : prevX;
    }

    @SuppressWarnings("unused")
    protected int getOffsetY(CardView prev, CardView next, Integer prevY) {
        return prev == null ? getStartY() : prevY;
    }

    @SuppressWarnings("unused")
    protected int getOffsetZ(CardView prev, CardView next, Integer prevZ) {
        return prev == null ? getStartZ() : prevZ + 1;
    }

    private void positionWidget(Widget image, int x, int y, int z) {
        Style style = image.getElement().getStyle();
        style.setLeft(x, Style.Unit.PX);
        style.setTop(y, Style.Unit.PX);
        style.setZIndex(z);
    }

    protected static class CardViewCardTransformer implements Function<CardView, Card> {
        @Nullable @Override public Card apply(@Nullable CardView input) {
            assert input != null;
            return input.card;
        }
    }
}
