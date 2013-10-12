/*
 * Preferanser is a program to simulate and calculate Russian Preferans Card game deals.
 *
 *     Copyright (C) 2013  Yuriy Lazarev <Yuriy.Lazarev@gmail.com>
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see [http://www.gnu.org/licenses/].
 */

package com.preferanser.client.application.table;

import com.google.common.base.Function;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.preferanser.client.application.table.layout.CardLayout;
import com.preferanser.client.application.table.layout.CenterCardLayout;
import com.preferanser.client.application.table.layout.EastCardLayout;
import com.preferanser.client.application.table.layout.HorizontalCardLayout;
import com.preferanser.shared.Card;
import com.preferanser.shared.TableLocation;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Map;

import static com.google.common.collect.Collections2.transform;
import static com.google.common.collect.Maps.newHashMapWithExpectedSize;
import static com.preferanser.shared.TableLocation.*;

/**
 * Table view
 */
public class TableView extends ViewWithUiHandlers<TableUiHandlers> implements TablePresenter.TableView {

    public interface Binder extends UiBinder<Widget, TableView> {}

    private final Map<Card, CardView> cardViewMap = newHashMapWithExpectedSize(32);
    private final Map<TableLocation, FlowPanel> locationPanelMap = newHashMapWithExpectedSize(5);
    private final Map<TableLocation, CardLayout> locationLayoutMap = newHashMapWithExpectedSize(5);

    private ImageDragController imageDragController = new ImageDragController(Document.get());

    @Inject
    public TableView(Binder uiBinder) {
        initWidget(uiBinder.createAndBindUi(this));
        populateCardImagesMap();
        populateLocationPanelMap();
        populateLocationLayoutMap();
        RootPanel rootPanel = RootPanel.get();
        handleMouseUp(rootPanel);
        handleMouseMove(rootPanel);
        for (CardView cardView : cardViewMap.values()) {
            handleMouseDown(cardView.image);
            handleDragStart(cardView.image);
        }
    }

    @Override
    public void displayCards(TableLocation location, Card... cards) {
        HasWidgets panel = locationPanelMap.get(location);
        for (Card card : cards) {
            displayCard(panel, card);
        }
        CardLayout cardLayout = locationLayoutMap.get(location);
        cardLayout.apply(transform(Arrays.asList(cards), new Function<Card, CardView>() {
            @Nullable @Override public CardView apply(@Nullable Card input) {
                return input == null ? null : cardViewMap.get(input);
            }
        }));
    }

    private void displayCard(HasWidgets panel, Card card) {
        CardView cardView = cardViewMap.get(card);
        // noinspection GWTStyleCheck
        cardView.image.removeStyleName("not-visible");
        if (!cardView.image.getParent().equals(panel)) {
            panel.add(cardView.image);
        }
    }

    private void handleMouseUp(RootPanel rootPanel) {
        rootPanel.addDomHandler(new MouseUpHandler() {
            @Override public void onMouseUp(MouseUpEvent event) {
                if (imageDragController.isDrag())
                    imageDragController.stopDrag();
            }
        }, MouseUpEvent.getType());
    }

    private void handleMouseMove(RootPanel rootPanel) {
        rootPanel.addDomHandler(new MouseMoveHandler() {
            @Override public void onMouseMove(MouseMoveEvent event) {
                if (imageDragController.isDrag())
                    imageDragController.updateImagePosition(event);
            }
        }, MouseMoveEvent.getType());
    }

    private void handleDragStart(Image image) {
        image.addDragStartHandler(new DragStartHandler() {
            @Override public void onDragStart(DragStartEvent event) {
                event.stopPropagation();
                event.preventDefault();
            }
        });
    }

    private void handleMouseDown(final Image image) {
        image.addMouseDownHandler(new MouseDownHandler() {
            @Override public void onMouseDown(MouseDownEvent event) {
                imageDragController.startDrag(image, event);
                putCardImageOnTop(image);
            }
        });
    }

    private void putCardImageOnTop(Image image) {
        image.getElement().getStyle().setZIndex(getMaxCardZIndex() + 1);
    }

    private int getMaxCardZIndex() {
        int maxZIndex = 0;
        for (CardView cardView : cardViewMap.values()) {
            int zIndex;
            try {
                zIndex = Integer.parseInt(cardView.image.getElement().getStyle().getZIndex());
            } catch (NumberFormatException e) {
                zIndex = 0;
            }
            if (maxZIndex < zIndex) {
                maxZIndex = zIndex;
            }
        }
        return maxZIndex;
    }

    @UiHandler("dealButton") void onDealButtonClicked(@SuppressWarnings("unused") ClickEvent event) {
        getUiHandlers().dealCards();
    }

    private void populateCardImagesMap() {
        cardViewMap.put(Card.CLUB_SEVEN, new CardView(Card.CLUB_SEVEN, c7));
        cardViewMap.put(Card.SPADE_SEVEN, new CardView(Card.SPADE_SEVEN, s7));
        cardViewMap.put(Card.DIAMOND_SEVEN, new CardView(Card.DIAMOND_SEVEN, d7));
        cardViewMap.put(Card.HEART_SEVEN, new CardView(Card.HEART_SEVEN, h7));
        cardViewMap.put(Card.CLUB_EIGHT, new CardView(Card.CLUB_EIGHT, c8));
        cardViewMap.put(Card.SPADE_EIGHT, new CardView(Card.SPADE_EIGHT, s8));
        cardViewMap.put(Card.DIAMOND_EIGHT, new CardView(Card.DIAMOND_EIGHT, d8));
        cardViewMap.put(Card.HEART_EIGHT, new CardView(Card.HEART_EIGHT, h8));
        cardViewMap.put(Card.CLUB_NINE, new CardView(Card.CLUB_NINE, c9));
        cardViewMap.put(Card.SPADE_NINE, new CardView(Card.SPADE_NINE, s9));
        cardViewMap.put(Card.DIAMOND_NINE, new CardView(Card.DIAMOND_NINE, d9));
        cardViewMap.put(Card.HEART_NINE, new CardView(Card.HEART_NINE, h9));
        cardViewMap.put(Card.CLUB_TEN, new CardView(Card.CLUB_TEN, c10));
        cardViewMap.put(Card.SPADE_TEN, new CardView(Card.SPADE_TEN, s10));
        cardViewMap.put(Card.DIAMOND_TEN, new CardView(Card.DIAMOND_TEN, d10));
        cardViewMap.put(Card.HEART_TEN, new CardView(Card.HEART_TEN, h10));
        cardViewMap.put(Card.CLUB_JACK, new CardView(Card.CLUB_JACK, cj));
        cardViewMap.put(Card.SPADE_JACK, new CardView(Card.SPADE_JACK, sj));
        cardViewMap.put(Card.DIAMOND_JACK, new CardView(Card.DIAMOND_JACK, dj));
        cardViewMap.put(Card.HEART_JACK, new CardView(Card.HEART_JACK, hj));
        cardViewMap.put(Card.CLUB_QUEEN, new CardView(Card.CLUB_QUEEN, cq));
        cardViewMap.put(Card.SPADE_QUEEN, new CardView(Card.SPADE_QUEEN, sq));
        cardViewMap.put(Card.DIAMOND_QUEEN, new CardView(Card.DIAMOND_QUEEN, dq));
        cardViewMap.put(Card.HEART_QUEEN, new CardView(Card.HEART_QUEEN, hq));
        cardViewMap.put(Card.CLUB_KING, new CardView(Card.CLUB_KING, ck));
        cardViewMap.put(Card.SPADE_KING, new CardView(Card.SPADE_KING, sk));
        cardViewMap.put(Card.DIAMOND_KING, new CardView(Card.DIAMOND_KING, dk));
        cardViewMap.put(Card.HEART_KING, new CardView(Card.HEART_KING, hk));
        cardViewMap.put(Card.CLUB_ACE, new CardView(Card.CLUB_ACE, ca));
        cardViewMap.put(Card.SPADE_ACE, new CardView(Card.SPADE_ACE, sa));
        cardViewMap.put(Card.DIAMOND_ACE, new CardView(Card.DIAMOND_ACE, da));
        cardViewMap.put(Card.HEART_ACE, new CardView(Card.HEART_ACE, ha));
    }

    private void populateLocationPanelMap() {
        locationPanelMap.put(NORTH, northPanel);
        locationPanelMap.put(EAST, eastPanel);
        locationPanelMap.put(SOUTH, southPanel);
        locationPanelMap.put(WEST, westPanel);
        locationPanelMap.put(CENTER, centerPanel);
    }

    private void populateLocationLayoutMap() {
        locationLayoutMap.put(NORTH, new HorizontalCardLayout(northPanel));
        locationLayoutMap.put(EAST, new EastCardLayout(eastPanel));
        locationLayoutMap.put(SOUTH, new HorizontalCardLayout(southPanel));
        locationLayoutMap.put(WEST, new WestCardLayout(westPanel));
        locationLayoutMap.put(CENTER, new CenterCardLayout(centerPanel));
    }

    @UiField Button dealButton;
    @UiField FlowPanel northPanel;
    @UiField FlowPanel eastPanel;
    @UiField FlowPanel southPanel;
    @UiField FlowPanel westPanel;
    @UiField FlowPanel centerPanel;

    @UiField Image c7;
    @UiField Image s7;
    @UiField Image d7;
    @UiField Image h7;
    @UiField Image c8;
    @UiField Image s8;
    @UiField Image d8;
    @UiField Image h8;
    @UiField Image c9;
    @UiField Image s9;
    @UiField Image d9;
    @UiField Image h9;
    @UiField Image c10;
    @UiField Image s10;
    @UiField Image d10;
    @UiField Image h10;
    @UiField Image cj;
    @UiField Image sj;
    @UiField Image dj;
    @UiField Image hj;
    @UiField Image cq;
    @UiField Image sq;
    @UiField Image dq;
    @UiField Image hq;
    @UiField Image ck;
    @UiField Image sk;
    @UiField Image dk;
    @UiField Image hk;
    @UiField Image ca;
    @UiField Image sa;
    @UiField Image da;
    @UiField Image ha;
}
