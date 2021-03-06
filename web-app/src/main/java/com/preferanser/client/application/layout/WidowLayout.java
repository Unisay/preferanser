package com.preferanser.client.application.layout;

import com.google.gwt.user.client.ui.Panel;
import com.preferanser.client.application.widgets.CardWidget;

import java.util.Collection;
import java.util.Iterator;

public class WidowLayout extends PanelLayout<CardWidget> {

    private final int cardWidth;
    private final int cardHeight;

    public WidowLayout(Panel panel, int cardWidth, int cardHeight) {
        super(panel);
        this.cardWidth = cardWidth;
        this.cardHeight = cardHeight;
    }

    @Override
    public void layoutPanelWidgets(Collection<CardWidget> widgets) {
        Iterator<CardWidget> iterator = widgets.iterator();
        int x = panel.getAbsoluteLeft() + (getWidth() - cardWidth) / 2;
        int y = panel.getAbsoluteTop() + (getHeight() - cardHeight) / 2;
        if (iterator.hasNext()) {
            positionWidget(iterator.next(), x - cardWidth / 2, y, 1);
            if (iterator.hasNext())
                positionWidget(iterator.next(), x + cardWidth / 2, y, 2);
        }
    }

}
