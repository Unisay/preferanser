package com.preferanser.client.application.table.layout;

import com.google.gwt.user.client.ui.Panel;
import com.preferanser.client.application.table.CardView;
import com.preferanser.shared.Card;

import java.util.Collection;
import java.util.List;

import static com.google.common.collect.Collections2.transform;

public class HorizontalCardLayout extends PanelCardLayout {

    private final int imageWidth;
    private final int minOffsetDiffSuit;
    private final int maxOffsetDiffSuit;
    private final int minOffsetSameSuit;
    private final int maxOffsetSameSuit;
    private int sameSuitOffsetX;
    private int diffSuitOffsetX;
    private int sameSuitOffsetCount;
    private int diffSuitOffsetCount;

    public HorizontalCardLayout(Panel panel, int imageWidth) {
        super(panel);
        this.imageWidth = imageWidth;
        this.minOffsetSameSuit = 24;
        this.maxOffsetSameSuit = 2 * minOffsetSameSuit;
        this.minOffsetDiffSuit = imageWidth;
        this.maxOffsetDiffSuit = imageWidth + imageWidth / 2;
    }

    @Override
    protected void positionWidgets(List<CardView> cardViews) {
        Collection<Card> cards = transform(cardViews, new CardViewCardTransformer());
        diffSuitOffsetCount = countDiffSuitOffsets(cards);
        sameSuitOffsetCount = countSameSuitOffsets(cards);
        diffSuitOffsetX = calculateDiffSuitOffsetX();
        sameSuitOffsetX = calculateSameSuitOffsetX();
        super.positionWidgets(cardViews);
    }

    @Override
    protected int getStartX() {
        return (getWidth() - getCardsWidth()) / 2;
    }

    @Override
    protected int getDeltaX(CardView prev, CardView next) {
        return prev.card.getSuit() == next.card.getSuit()
                ? sameSuitOffsetX
                : diffSuitOffsetX;
    }

    private int getCardsWidth() {
        return sameSuitOffsetCount * sameSuitOffsetX + diffSuitOffsetCount * diffSuitOffsetX + imageWidth;
    }

    private int calculateDiffSuitOffsetX() {
        int dx;

        int availableSpace = getDisposableWidth() - getSameSuitWidth();
        if (availableSpace > 0) {
            // Cards are narrower than panel
            dx = diffSuitOffsetCount == 0 ? maxOffsetDiffSuit : availableSpace / diffSuitOffsetCount;
        } else {
            // Cards are wider than panel
            dx = maxOffsetDiffSuit - availableSpace / diffSuitOffsetCount;
        }

        if (dx > maxOffsetDiffSuit) {
            dx = maxOffsetDiffSuit;
        }
        if (dx < minOffsetDiffSuit) {
            dx = minOffsetDiffSuit;
        }
        return dx;
    }

    private int calculateSameSuitOffsetX() {
        int dx = minOffsetSameSuit;
        int occupiedWidth = getSameSuitWidth() + diffSuitOffsetCount * diffSuitOffsetX;
        int availableSpace = getDisposableWidth() - occupiedWidth;

        if (availableSpace > 0) {
            dx += sameSuitOffsetCount == 0 ? maxOffsetSameSuit : availableSpace / sameSuitOffsetCount;
        }

        if (dx < minOffsetSameSuit) {
            dx = minOffsetSameSuit;
        }
        if (dx > maxOffsetSameSuit) {
            dx = maxOffsetSameSuit;
        }
        return dx;
    }

    private int getSameSuitWidth() {
        return sameSuitOffsetCount * minOffsetSameSuit + imageWidth;
    }

    protected static int countSameSuitOffsets(Collection<Card> cards) {
        int count = 0;
        Card prev = null;
        for (Card card : cards) {
            if (prev != null && prev.getSuit() == card.getSuit()) {
                count++;
            }
            prev = card;
        }
        return count;
    }

    protected static int countDiffSuitOffsets(Collection<Card> cards) {
        int count = 0;
        Card prev = null;
        for (Card card : cards) {
            if (prev != null && prev.getSuit() != card.getSuit()) {
                count++;
            }
            prev = card;
        }
        return count;
    }

}
