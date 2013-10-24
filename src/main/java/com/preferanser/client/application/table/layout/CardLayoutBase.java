package com.preferanser.client.application.table.layout;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.gwt.dom.client.Style;
import com.preferanser.client.application.widgets.CardWidget;
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
    public void apply(Collection<CardWidget> cardWidgets) {
        if (cardWidgets == null || cardWidgets.isEmpty())
            return;

        List<CardWidget> views = Lists.newArrayList(cardWidgets);
        sortCards(views);
        positionWidgets(views);
    }

    protected void sortCards(List<CardWidget> views) {
        sort(views, new Comparator<CardWidget>() {
            @Override public int compare(CardWidget view1, CardWidget view2) {
                return compareCards(view1.getCard(), view2.getCard());
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

    protected void positionWidgets(List<CardWidget> cardWidgets) {
        Integer x = null, y = null, z = null;
        CardWidget prev = null;
        for (CardWidget next : cardWidgets) {
            x = getOffsetX(prev, next, x);
            y = getOffsetY(prev, next, y);
            z = getOffsetZ(prev, next, z);
            positionWidget(next, x, y, z);
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
    protected int getOffsetX(CardWidget prev, CardWidget next, Integer prevX) {
        return prev == null ? getStartX() : prevX + getDeltaX(prev, next);
    }

    @SuppressWarnings("unused")
    protected int getDeltaX(CardWidget prev, CardWidget next) {
        return 0;
    }

    @SuppressWarnings("unused")
    protected int getOffsetY(CardWidget prev, CardWidget next, Integer prevY) {
        return prev == null ? getStartY() : prevY + getDeltaY(prev, next);
    }

    @SuppressWarnings("unused")
    protected int getDeltaY(CardWidget prev, CardWidget next) {
        return 0;
    }

    @SuppressWarnings("unused")
    protected int getOffsetZ(CardWidget prev, CardWidget next, Integer prevZ) {
        return prev == null ? getStartZ() : prevZ + getDeltaZ(prev, next);
    }

    @SuppressWarnings("unused")
    protected int getDeltaZ(CardWidget prev, CardWidget next) {
        return 1;
    }

    protected void positionWidget(CardWidget cardWidget, int x, int y, int z) {
        Style style = cardWidget.getElement().getStyle();
        style.setLeft(x, Style.Unit.PX);
        style.setTop(y, Style.Unit.PX);
        style.setZIndex(z);
    }

    protected class CardWidgetCardTransformer implements Function<CardWidget, Card> {
        @Nullable @Override public Card apply(@Nullable CardWidget input) {
            assert input != null;
            return input.getCard();
        }
    }
}
