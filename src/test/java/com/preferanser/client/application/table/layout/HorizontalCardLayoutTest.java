package com.preferanser.client.application.table.layout;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.googlecode.gwt.test.GwtModule;
import com.googlecode.gwt.test.GwtTest;
import com.preferanser.client.application.table.CardView;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static com.preferanser.shared.Card.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

@GwtModule("com.preferanser.Preferanser")
public class HorizontalCardLayoutTest extends GwtTest {

    private HorizontalCardLayout layout;

    @Before
    public void setUp() throws Exception {
        FlowPanel panel = new FlowPanel();
        layout = new HorizontalCardLayout(panel);
    }

    @Test
    public void testApply() throws Exception {
        List<CardView> cardViews = Arrays.asList(
                new CardView(CLUB_ACE, positionImage(new Image(), 0, 0, 0)),
                new CardView(SPADE_KING, positionImage(new Image(), 0, 0, 0)),
                new CardView(SPADE_ACE, positionImage(new Image(), 0, 0, 0))
        );

        layout.apply(cardViews);

        List<CardView> expectedCardViews = Arrays.asList(
                new CardView(CLUB_ACE, positionImage(new Image(), 0, 0, 0)),
                new CardView(SPADE_KING, positionImage(new Image(), 10, 0, 1)),
                new CardView(SPADE_ACE, positionImage(new Image(), 20, 0, 2))
        );

        assertThat(cardViews, equalTo(expectedCardViews));
    }

    private Image positionImage(Image image, int left, int top, int z) {
        Style style = image.getElement().getStyle();
        style.setLeft(left, Style.Unit.PX);
        style.setTop(top, Style.Unit.PX);
        style.setZIndex(z);
        return image;
    }

}