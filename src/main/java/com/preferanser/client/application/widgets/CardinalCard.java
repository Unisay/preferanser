package com.preferanser.client.application.widgets;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.preferanser.shared.Cardinal;

public class CardinalCard implements IsWidget {

    private Cardinal cardinal;
    private CardWidget cardWidget;

    public CardinalCard(Cardinal cardinal, CardWidget cardWidget) {
        this.cardinal = cardinal;
        this.cardWidget = cardWidget;
    }

    public Cardinal getCardinal() {
        return cardinal;
    }

    public CardWidget getCardWidget() {
        return cardWidget;
    }

    @Override
    public Widget asWidget() {
        return cardWidget;
    }

}
