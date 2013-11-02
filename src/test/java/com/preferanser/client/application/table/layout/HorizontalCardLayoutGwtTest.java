package com.preferanser.client.application.table.layout;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.FlowPanel;
import com.googlecode.gwt.test.GwtModule;
import com.googlecode.gwt.test.GwtTest;
import com.preferanser.client.application.widgets.CardWidget;
import com.preferanser.shared.Card;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static com.preferanser.shared.Card.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

@GwtModule("com.preferanser.Preferanser")
public class HorizontalCardLayoutGwtTest extends GwtTest {

    private HorizontalLayout layout;

    @Before
    public void setUp() throws Exception {
        FlowPanel panel = new FlowPanel();
        layout = new HorizontalLayout(panel, 40);
    }

    @Test
    public void testApply() throws Exception {
        List<CardWidget> cardWidgets = Arrays.asList(
                positionImage(CLUB_ACE, 0, 0, 0),
                positionImage(SPADE_KING, 0, 0, 0),
                positionImage(SPADE_ACE, 0, 0, 0)
        );

        layout.apply(cardWidgets);

        List<CardWidget> expectedCardWidgets = Arrays.asList(
                positionImage(CLUB_ACE, 0, 0, 0),
                positionImage(SPADE_KING, 10, 0, 1),
                positionImage(SPADE_ACE, 20, 0, 2)
        );

        assertThat(cardWidgets, equalTo(expectedCardWidgets));
    }

    private CardWidget positionImage(Card card, int left, int top, int z) {
        CardWidget cardWidget = new CardWidget();
        cardWidget.setCard(card);
        Style style = cardWidget.getElement().getStyle();
        style.setLeft(left, Style.Unit.PX);
        style.setTop(top, Style.Unit.PX);
        style.setZIndex(z);
        return cardWidget;
    }

}