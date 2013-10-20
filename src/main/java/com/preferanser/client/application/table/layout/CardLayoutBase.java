package com.preferanser.client.application.table.layout;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.Widget;
import com.preferanser.client.application.table.CardView;
import com.preferanser.shared.Card;
import com.preferanser.shared.Rank;
import com.preferanser.shared.Suit;

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
                return compareCards(view1.card, view2.card);
            }
        });
    }

    protected int compareCards(Card card1, Card card2) {
        int suitResult = compareSuits(card1.getSuit(), card2.getSuit());
        if (0 == suitResult) {
            return compareRanks(card2.getRank(), card1.getRank());
        } else {
            return suitResult;
        }
    }

    protected int compareRanks(Rank rank1, Rank rank2) {
        return rank1.compareTo(rank2);
    }

    protected int compareSuits(Suit suit1, Suit suit2) {
        return suit1.compareTo(suit2);
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
        return prev == null ? getStartX() : prevX + getDeltaX(prev, next);
    }

    @SuppressWarnings("unused")
    protected int getDeltaX(CardView prev, CardView next) {
        return 0;
    }

    @SuppressWarnings("unused")
    protected int getOffsetY(CardView prev, CardView next, Integer prevY) {
        return prev == null ? getStartY() : prevY + getDeltaY(prev, next);
    }

    @SuppressWarnings("unused")
    protected int getDeltaY(CardView prev, CardView next) {
        return 0;
    }

    @SuppressWarnings("unused")
    protected int getOffsetZ(CardView prev, CardView next, Integer prevZ) {
        return prev == null ? getStartZ() : prevZ + getDeltaZ(prev, next);
    }

    @SuppressWarnings("unused")
    protected int getDeltaZ(CardView prev, CardView next) {
        return 1;
    }

    protected void positionWidget(Widget image, int x, int y, int z) {
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
