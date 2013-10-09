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

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.preferanser.shared.Card;
import com.preferanser.shared.TableLocation;

import java.util.Map;

import static com.google.common.collect.Maps.newHashMapWithExpectedSize;

/**
 * Table view
 */
public class TableView extends ViewWithUiHandlers<TableUiHandlers> implements TablePresenter.TableView {

    public interface Binder extends UiBinder<Widget, TableView> {}

    private final Map<Card, Image> cardImageMap = newHashMapWithExpectedSize(32);
    private final Map<TableLocation, FlowPanel> tableLocationPanelMap = newHashMapWithExpectedSize(5);

    private Image draggedImage;

    @Inject
    public TableView(Binder uiBinder) {
        initWidget(uiBinder.createAndBindUi(this));
        populateCardImagesMap();
        populateTableLocationMap();
        RootPanel rootPanel = RootPanel.get();
        handleMouseUp(rootPanel);
        handleMouseMove(rootPanel);
        for (final Image image : cardImageMap.values()) {
            handleMouseDown(image);
            handleDragStart(image);
        }
    }

    @Override
    public void displayCards(TableLocation location, Card... cards) {
        for (Card card : cards) {
            HasWidgets panel = tableLocationPanelMap.get(location);
            displayCard(panel, card);
        }
    }

    @SuppressWarnings("GWTStyleCheck")
    private void displayCard(HasWidgets panel, Card card) {
        Image image = cardImageMap.get(card);
        image.removeStyleName("not-visible");
        if (!image.getParent().equals(panel)) {
            image.removeFromParent();
            panel.add(image);
        }
    }

    private void handleMouseUp(RootPanel rootPanel) {
        rootPanel.addDomHandler(new MouseUpHandler() {
            @Override public void onMouseUp(MouseUpEvent event) {
                draggedImage = null;
            }
        }, MouseUpEvent.getType());
    }

    private void handleMouseMove(RootPanel rootPanel) {
        rootPanel.addDomHandler(new MouseMoveHandler() {
            @Override public void onMouseMove(MouseMoveEvent event) {
                if (draggedImage != null) {
                    Style style = draggedImage.getElement().getStyle();
                    style.setLeft(event.getClientX(), Style.Unit.PX);
                    style.setTop(event.getClientY(), Style.Unit.PX);
                }
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
                draggedImage = image;
            }
        });
    }

    @UiHandler("dealButton") void onDealButtonClicked(@SuppressWarnings("unused") ClickEvent event) {
        getUiHandlers().dealCards();
    }

    private void populateCardImagesMap() {
        cardImageMap.put(Card.CLUB_SEVEN, c7);
        cardImageMap.put(Card.SPADE_SEVEN, s7);
        cardImageMap.put(Card.DIAMOND_SEVEN, d7);
        cardImageMap.put(Card.HEART_SEVEN, h7);
        cardImageMap.put(Card.CLUB_EIGHT, c8);
        cardImageMap.put(Card.SPADE_EIGHT, s8);
        cardImageMap.put(Card.DIAMOND_EIGHT, d8);
        cardImageMap.put(Card.HEART_EIGHT, h8);
        cardImageMap.put(Card.CLUB_NINE, c9);
        cardImageMap.put(Card.SPADE_NINE, s9);
        cardImageMap.put(Card.DIAMOND_NINE, d9);
        cardImageMap.put(Card.HEART_NINE, h9);
        cardImageMap.put(Card.CLUB_TEN, c10);
        cardImageMap.put(Card.SPADE_TEN, s10);
        cardImageMap.put(Card.DIAMOND_TEN, d10);
        cardImageMap.put(Card.HEART_TEN, h10);
        cardImageMap.put(Card.CLUB_JACK, cj);
        cardImageMap.put(Card.SPADE_JACK, sj);
        cardImageMap.put(Card.DIAMOND_JACK, dj);
        cardImageMap.put(Card.HEART_JACK, hj);
        cardImageMap.put(Card.CLUB_QUEEN, cq);
        cardImageMap.put(Card.SPADE_QUEEN, sq);
        cardImageMap.put(Card.DIAMOND_QUEEN, dq);
        cardImageMap.put(Card.HEART_QUEEN, hq);
        cardImageMap.put(Card.CLUB_KING, ck);
        cardImageMap.put(Card.SPADE_KING, sk);
        cardImageMap.put(Card.DIAMOND_KING, dk);
        cardImageMap.put(Card.HEART_KING, hk);
        cardImageMap.put(Card.CLUB_ACE, ca);
        cardImageMap.put(Card.SPADE_ACE, sa);
        cardImageMap.put(Card.DIAMOND_ACE, da);
        cardImageMap.put(Card.HEART_ACE, ha);
    }

    private void populateTableLocationMap() {
        tableLocationPanelMap.put(TableLocation.NORTH, northPanel);
        tableLocationPanelMap.put(TableLocation.EAST, eastPanel);
        tableLocationPanelMap.put(TableLocation.SOUTH, southPanel);
        tableLocationPanelMap.put(TableLocation.WEST, westPanel);
        tableLocationPanelMap.put(TableLocation.CENTER, centerPanel);
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
