package com.preferanser.client.application.table.layout;

import com.google.gwt.user.client.ui.Panel;
import com.preferanser.client.application.table.CardView;
import com.preferanser.shared.Card;
import com.preferanser.shared.Suit;

import java.util.*;

import static com.google.common.collect.Collections2.transform;

public class WestCardLayout extends PanelCardLayout {

    private final int imageWidth;
    private final int imageHeight;
    private int sameSuitOffsetX;
    private int diffSuitOffsetY;
    private int minSameSuitOffsetX;
    private int maxSameSuitOffsetX;
    private int minDiffSuitOffsetY;
    private int maxDiffSuitOffsetY;

    public WestCardLayout(Panel panel, int imageWidth, int imageHeight) {
        super(panel);
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        this.minSameSuitOffsetX = 24;
        this.maxSameSuitOffsetX = imageWidth - minSameSuitOffsetX;
        this.minDiffSuitOffsetY = 85;
        this.maxDiffSuitOffsetY = imageHeight;
    }

    @Override
    protected void positionWidgets(List<CardView> cardViews) {
        Collection<Card> cards = transform(cardViews, new CardViewCardTransformer());
        sameSuitOffsetX = calculateSameSuitOffsetX(cards);
        diffSuitOffsetY = calculateDiffSuitOffsetY(cards);
        super.positionWidgets(cardViews);
    }

    private int calculateSameSuitOffsetX(Collection<Card> cards) {
        int dx = minSameSuitOffsetX;
        int maxCardsSameSuit = getMaxCardsSameSuit(cards);
        int maxCardsOffsetCount = maxCardsSameSuit - 1;

        int minOccupiedWidth = maxCardsOffsetCount * minSameSuitOffsetX + imageWidth;
        int extraWidth = getDisposableWidth() - minOccupiedWidth;

        assert extraWidth >= 0 : "Cards are wider than panel";

        if (extraWidth > 0 && maxCardsOffsetCount > 0) {
            dx += extraWidth / maxCardsOffsetCount;
        }

        if (dx > maxSameSuitOffsetX) {
            dx = maxSameSuitOffsetX;
        }

        return dx;
    }

    private int calculateDiffSuitOffsetY(Collection<Card> cards) {
        int dy = minDiffSuitOffsetY;

        int suitCount = getSuitCount(cards);
        assert suitCount > 0 : "Suit count < 0";
        int suitOffsetCount = suitCount - 1;

        int minOccupiedHeight = suitOffsetCount * minDiffSuitOffsetY + imageHeight;
        int extraHeight = getDisposableHeight() - minOccupiedHeight;

        assert extraHeight >= 0 : "Cards are taller than panel";

        if (extraHeight > 0 && suitOffsetCount > 0) {
            dy += extraHeight / suitOffsetCount;
        }

        if (dy > maxDiffSuitOffsetY) {
            dy = maxDiffSuitOffsetY;
        }

        return dy;
    }

    @Override
    protected int getOffsetX(CardView prev, CardView next, Integer prevX) {
        if (prev == null)
            return getStartX();
        assert prevX != null;

        if (prev.card.getSuit() == next.card.getSuit()) {
            return prevX + sameSuitOffsetX;
        } else {
            return getStartX();
        }
    }

    @Override
    protected int getOffsetY(CardView prev, CardView next, Integer prevY) {
        if (prev == null)
            return getStartY();
        assert prevY != null;

        if (prev.card.getSuit() == next.card.getSuit()) {
            return prevY;
        } else {
            return prevY + diffSuitOffsetY;
        }
    }

    private int getSuitCount(Collection<Card> cards) {
        Set<Suit> suits = EnumSet.noneOf(Suit.class);
        for (Card card : cards) {
            suits.add(card.getSuit());
        }
        return suits.size();
    }

    private int getMaxCardsSameSuit(Collection<Card> cards) {
        Map<Suit, Integer> countMap = new EnumMap<Suit, Integer>(Suit.class);
        for (Suit suit : Suit.values()) {
            countMap.put(suit, 0);
        }
        for (Card card : cards) {
            countMap.put(card.getSuit(), countMap.get(card.getSuit()) + 1);
        }
        int max = 0;
        for (Integer count : countMap.values()) {
            if (max < count) {
                max = count;
            }
        }
        return max;
    }

}
