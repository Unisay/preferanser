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
import com.google.common.collect.BiMap;
import com.google.common.collect.EnumHashBiMap;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
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
import com.preferanser.client.geom.Point;
import com.preferanser.client.geom.Rect;
import com.preferanser.shared.Card;
import com.preferanser.shared.TableLocation;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.logging.Logger;

import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Lists.newArrayList;
import static com.preferanser.shared.TableLocation.*;

/**
 * Table view
 */
public class TableView extends ViewWithUiHandlers<TableUiHandlers> implements TablePresenter.TableView {

    private static Logger log = Logger.getLogger("TableView");

    public interface Binder extends UiBinder<Widget, TableView> {}

    private final BiMap<Card, Image> cardViewMap = EnumHashBiMap.create(Card.class);
    private final BiMap<TableLocation, FlowPanel> locationPanelMap = EnumHashBiMap.create(TableLocation.class);
    private final BiMap<TableLocation, CardLayout> locationLayoutMap = EnumHashBiMap.create(TableLocation.class);

    private ImageDragController imageDragController = new ImageDragController(Document.get());

    @Inject
    public TableView(Binder uiBinder, GQuerySelectors selectors) {
        initWidget(uiBinder.createAndBindUi(this));
        disableStandardDragging(selectors.getAllDivsAndImages().elements());
        populateCardImagesMap();
        populateLocationPanelMap();
        populateLocationLayoutMap();
        RootPanel rootPanel = RootPanel.get();
        installMouseUpHandler(rootPanel);
        installMouseUpHandler(locationPanelMap.values());
        installMouseMoveHandler(rootPanel);
        for (Image image : cardViewMap.values()) {
            installMouseDownHandler(image);
            handleDragStart(image);
        }
    }

    @Override
    public void displayCards(TableLocation location, Card... cards) {
        HasWidgets panel = locationPanelMap.get(location);
        for (Card card : cards) {
            displayCard(panel, card);
        }
        layoutLocation(location);
    }

    private void layoutLocation(TableLocation location) {
        FlowPanel panel = locationPanelMap.get(location);
        CardLayout cardLayout = locationLayoutMap.get(location);
        cardLayout.apply(newArrayList(transform(panel, new Function<Widget, CardView>() {
            @Nullable @Override public CardView apply(@Nullable Widget widget) {
                if (widget instanceof Image) {
                    Image image = (Image) widget;
                    Card card = cardViewMap.inverse().get(image);
                    assert card != null : "Image is not card. Can't apply layout: " + image;
                    return new CardView(card, image);
                }
                return null;
            }
        })));
    }

    private void displayCard(HasWidgets panel, Card card) {
        Image image = cardViewMap.get(card);
        // noinspection GWTStyleCheck
        image.removeStyleName("not-visible");
        if (!image.getParent().equals(panel)) {
            panel.add(image);
        }
    }

    private void disableStandardDragging(Element[] elements) {
        for (Element element : elements) {
            element.setDraggable(Element.DRAGGABLE_FALSE);
        }
    }

    private void installMouseDownHandler(final Image image) {
        image.addMouseDownHandler(new MouseDownHandler() {
            @Override public void onMouseDown(MouseDownEvent event) {
                imageDragController.startDrag(image, event);
                putCardImageOnTop(image);
            }
        });
    }

    private void installMouseMoveHandler(RootPanel rootPanel) {
        rootPanel.addDomHandler(new MouseMoveHandler() {
            @Override public void onMouseMove(MouseMoveEvent event) {
                if (imageDragController.isDrag())
                    imageDragController.updateImagePosition(event);
            }
        }, MouseMoveEvent.getType());
    }

    private void installMouseUpHandler(RootPanel rootPanel) {
        rootPanel.addDomHandler(new MouseUpHandler() {
            @Override public void onMouseUp(MouseUpEvent event) {
                if (imageDragController.isDrag()) {
                    imageDragController.stopDrag();
                }
            }
        }, MouseUpEvent.getType());
    }

    private void installMouseUpHandler(final Collection<FlowPanel> panels) {
        for (final FlowPanel sourcePanel : panels) {
            sourcePanel.addDomHandler(new MouseUpHandler() {
                @Override public void onMouseUp(MouseUpEvent event) {
                    if (imageDragController.isDrag()) {
                        Point cardCenter = Rect.FromWidget(imageDragController.getImage()).center();
                        for (final FlowPanel targetPanel : panels) {
                            if (Rect.FromWidget(targetPanel).contains(cardCenter)) {
                                Card card = cardViewMap.inverse().get(imageDragController.getImage());
                                TableLocation oldLocation = locationPanelMap.inverse().get(sourcePanel);
                                TableLocation newLocation = locationPanelMap.inverse().get(targetPanel);
                                log.finer("Card newLocation change: " + card + ": " + oldLocation + " -> " + newLocation);
                                getUiHandlers().onCardLocationChange(card, oldLocation, newLocation);
                                displayCards(newLocation, card);
                                layoutLocation(oldLocation);
                                return;
                            }
                        }
                    }
                }
            }, MouseUpEvent.getType());
        }
    }

    private void handleDragStart(Image image) {
        image.addDragStartHandler(new DragStartHandler() {
            @Override public void onDragStart(DragStartEvent event) {
                event.stopPropagation();
                event.preventDefault();
            }
        });
    }

    private void putCardImageOnTop(Image image) {
        image.getElement().getStyle().setZIndex(getMaxCardZIndex() + 1);
    }

    private int getMaxCardZIndex() {
        int maxZIndex = 0;
        for (Image image : cardViewMap.values()) {
            int zIndex;
            try {
                zIndex = Integer.parseInt(image.getElement().getStyle().getZIndex());
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
        getUiHandlers().onDealCards();
    }

    private void populateCardImagesMap() {
        cardViewMap.put(Card.CLUB_SEVEN, c7);
        cardViewMap.put(Card.SPADE_SEVEN, s7);
        cardViewMap.put(Card.DIAMOND_SEVEN, d7);
        cardViewMap.put(Card.HEART_SEVEN, h7);
        cardViewMap.put(Card.CLUB_EIGHT, c8);
        cardViewMap.put(Card.SPADE_EIGHT, s8);
        cardViewMap.put(Card.DIAMOND_EIGHT, d8);
        cardViewMap.put(Card.HEART_EIGHT, h8);
        cardViewMap.put(Card.CLUB_NINE, c9);
        cardViewMap.put(Card.SPADE_NINE, s9);
        cardViewMap.put(Card.DIAMOND_NINE, d9);
        cardViewMap.put(Card.HEART_NINE, h9);
        cardViewMap.put(Card.CLUB_TEN, c10);
        cardViewMap.put(Card.SPADE_TEN, s10);
        cardViewMap.put(Card.DIAMOND_TEN, d10);
        cardViewMap.put(Card.HEART_TEN, h10);
        cardViewMap.put(Card.CLUB_JACK, cj);
        cardViewMap.put(Card.SPADE_JACK, sj);
        cardViewMap.put(Card.DIAMOND_JACK, dj);
        cardViewMap.put(Card.HEART_JACK, hj);
        cardViewMap.put(Card.CLUB_QUEEN, cq);
        cardViewMap.put(Card.SPADE_QUEEN, sq);
        cardViewMap.put(Card.DIAMOND_QUEEN, dq);
        cardViewMap.put(Card.HEART_QUEEN, hq);
        cardViewMap.put(Card.CLUB_KING, ck);
        cardViewMap.put(Card.SPADE_KING, sk);
        cardViewMap.put(Card.DIAMOND_KING, dk);
        cardViewMap.put(Card.HEART_KING, hk);
        cardViewMap.put(Card.CLUB_ACE, ca);
        cardViewMap.put(Card.SPADE_ACE, sa);
        cardViewMap.put(Card.DIAMOND_ACE, da);
        cardViewMap.put(Card.HEART_ACE, ha);
    }

    private void populateLocationPanelMap() {
        locationPanelMap.put(NORTH, northPanel);
        locationPanelMap.put(EAST, eastPanel);
        locationPanelMap.put(SOUTH, southPanel);
        locationPanelMap.put(WEST, westPanel);
        locationPanelMap.put(CENTER, centerPanel);
    }

    private void populateLocationLayoutMap() {
        locationLayoutMap.put(NORTH, new HorizontalCardLayout(northPanel, c7.getWidth()));
        locationLayoutMap.put(EAST, new EastCardLayout(eastPanel));
        locationLayoutMap.put(SOUTH, new HorizontalCardLayout(southPanel, c7.getWidth()));
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
