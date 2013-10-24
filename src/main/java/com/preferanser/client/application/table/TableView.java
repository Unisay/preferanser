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
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.preferanser.client.application.PreferanserResources;
import com.preferanser.client.application.event.TurnChangeEvent;
import com.preferanser.client.application.table.layout.*;
import com.preferanser.client.application.widgets.CardWidget;
import com.preferanser.client.geom.Point;
import com.preferanser.client.geom.Rect;
import com.preferanser.shared.Card;
import com.preferanser.shared.Cardinal;
import com.preferanser.shared.TableLocation;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;
import java.util.logging.Logger;

import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Lists.newArrayList;
import static com.preferanser.shared.TableLocation.*;

/**
 * Table view
 */
public class TableView extends ViewWithUiHandlers<TableUiHandlers> implements TablePresenter.TableView, CardWidget.Handlers {

    private static final Logger log = Logger.getLogger("TableView");

    public interface Binder extends UiBinder<Widget, TableView> {}

    public interface Style extends CssResource {
        String card();
    }

    private final EventBus eventBus;

    private final PreferanserResources preferanserResources;
    private final BiMap<Card, CardWidget> cardWidgetBiMap = EnumHashBiMap.create(Card.class);

    private final BiMap<TableLocation, FlowPanel> locationPanelMap = EnumHashBiMap.create(TableLocation.class);

    private final BiMap<TableLocation, CardLayout> locationLayoutMap = EnumHashBiMap.create(TableLocation.class);
    private final Map<Cardinal, Label> cardinalTrickCount = Maps.newHashMapWithExpectedSize(Cardinal.values().length);
    private final Map<Cardinal, Label> cardinalTitle = Maps.newHashMapWithExpectedSize(Cardinal.values().length);
    private ImageDragController imageDragController = new ImageDragController(Document.get());


    @UiField Style style;
    @UiField Button dealButton;
    @UiField FlowPanel northPanel;
    @UiField FlowPanel eastPanel;
    @UiField FlowPanel southPanel;
    @UiField FlowPanel westPanel;
    @UiField FlowPanel centerPanel;

    @UiField Label trickCountNorth;
    @UiField Label trickCountEast;
    @UiField Label trickCountSouth;
    @UiField Label trickCountWest;

    @UiField Label titleNorth;
    @UiField Label titleEast;
    @UiField Label titleSouth;
    @UiField Label titleWest;

    @UiField Hyperlink sluffLink;

    @UiField Hyperlink northContractLink;
    @UiField Hyperlink eastContractLink;
    @UiField Hyperlink southContractLink;
    @UiField Hyperlink westContractLink;

    @Inject
    public TableView(Binder uiBinder, GQuerySelectors selectors, EventBus eventBus, PreferanserResources preferanserResources) {
        this.eventBus = eventBus;
        this.preferanserResources = preferanserResources;
        initWidget(uiBinder.createAndBindUi(this));
        disableStandardDragging(selectors.getAllDivsAndImages().elements());
        populateLocationPanelMap();
        populateLocationLayoutMap();
        populateCardinalTrickCounts();
        populateCardinalTitles();
        RootPanel rootPanel = RootPanel.get();
        installCenterPanelClickHandler();
        installMouseUpHandler(rootPanel);
        installMouseUpHandler(locationPanelMap.values());
        installMouseMoveHandler(rootPanel);
    }

    @Override
    public void displayTableCards(Multimap<TableLocation, Card> tableCards) {
        for (CardWidget cardWidget : cardWidgetBiMap.values()) {
            cardWidget.removeFromParent();
        }
        for (Map.Entry<TableLocation, Collection<Card>> entry : tableCards.asMap().entrySet()) {
            displayCards(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void setTrickCounts(Map<Cardinal, Integer> trickCounts) {
        for (Map.Entry<Cardinal, Integer> entry : trickCounts.entrySet()) {
            cardinalTrickCount.get(entry.getKey()).setText("" + entry.getValue());
        }
    }

    @Override
    public void onCardMouseDown(CardWidget cardWidget, MouseDownEvent event) {
        imageDragController.startDrag(cardWidget, event);
        putCardImageOnTop(cardWidget);
    }

    @Override
    public void onCardDragStart(CardWidget cardWidget, DragStartEvent event) {
        event.stopPropagation();
        event.preventDefault();
    }

    private void displayCards(TableLocation location, Iterable<Card> cards) {
        HasWidgets panel = locationPanelMap.get(location);
        for (Card card : cards) {
            displayCard(panel, card);
        }
        layoutLocation(location);
    }

    private void displayCard(HasWidgets panel, Card card) {
        CardWidget cardWidget = cardWidgetBiMap.get(card);
        panel.add(cardWidget);
    }

    private void layoutLocation(TableLocation location) {
        FlowPanel panel = locationPanelMap.get(location);
        CardLayout cardLayout = locationLayoutMap.get(location);
        cardLayout.apply(newArrayList(transform(panel, new Function<Widget, CardWidget>() {
            @Nullable @Override public CardWidget apply(@Nullable Widget widget) {
                if (widget instanceof CardWidget) {
                    return (CardWidget) widget;
                }
                return null;
            }
        })));
    }

    private void disableStandardDragging(Element[] elements) {
        for (Element element : elements) {
            element.setDraggable(Element.DRAGGABLE_FALSE);
        }
    }

    private void installCenterPanelClickHandler() {
        centerPanel.addDomHandler(new ClickHandler() {
            @Override public void onClick(ClickEvent event) {
                getUiHandlers().sluff();
            }
        }, ClickEvent.getType());
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
                        Point cardCenter = Rect.FromWidget(imageDragController.getCardWidget()).center();
                        for (final FlowPanel targetPanel : panels) {
                            if (Rect.FromWidget(targetPanel).contains(cardCenter)) {
                                Card card = cardWidgetBiMap.inverse().get(imageDragController.getCardWidget());
                                TableLocation oldLocation = locationPanelMap.inverse().get(sourcePanel);
                                TableLocation newLocation = locationPanelMap.inverse().get(targetPanel);
                                log.finer("Card newLocation change: " + card + ": " + oldLocation + " -> " + newLocation);
                                getUiHandlers().changeCardLocation(card, oldLocation, newLocation);
                                return;
                            }
                        }
                    }
                }
            }, MouseUpEvent.getType());
        }
    }

    private void putCardImageOnTop(Image image) {
        image.getElement().getStyle().setZIndex(getMaxCardZIndex() + 1);
    }

    private int getMaxCardZIndex() {
        int maxZIndex = 0;
        for (Image image : cardWidgetBiMap.values()) {
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
        getUiHandlers().dealCards();
    }

    @UiHandler("sluffLink") void onSluffLinkClicked(@SuppressWarnings("unused") ClickEvent event) {
        getUiHandlers().sluff();
    }

    @UiFactory CardWidget createCardWidget(Card card) {
        CardWidget cardWidget = new CardWidget(card);

        switch (card) {
            case SPADE_SEVEN:
                cardWidget.setResource(preferanserResources.s7());
                break;
            case CLUB_SEVEN:
                cardWidget.setResource(preferanserResources.c7());
                break;
            case DIAMOND_SEVEN:
                cardWidget.setResource(preferanserResources.d7());
                break;
            case HEART_SEVEN:
                cardWidget.setResource(preferanserResources.h7());
                break;

            case SPADE_EIGHT:
                cardWidget.setResource(preferanserResources.s8());
                break;
            case CLUB_EIGHT:
                cardWidget.setResource(preferanserResources.c8());
                break;
            case DIAMOND_EIGHT:
                cardWidget.setResource(preferanserResources.d8());
                break;
            case HEART_EIGHT:
                cardWidget.setResource(preferanserResources.h8());
                break;

            case SPADE_NINE:
                cardWidget.setResource(preferanserResources.s9());
                break;
            case CLUB_NINE:
                cardWidget.setResource(preferanserResources.c9());
                break;
            case DIAMOND_NINE:
                cardWidget.setResource(preferanserResources.d9());
                break;
            case HEART_NINE:
                cardWidget.setResource(preferanserResources.h9());
                break;

            case SPADE_TEN:
                cardWidget.setResource(preferanserResources.s10());
                break;
            case CLUB_TEN:
                cardWidget.setResource(preferanserResources.c10());
                break;
            case DIAMOND_TEN:
                cardWidget.setResource(preferanserResources.d10());
                break;
            case HEART_TEN:
                cardWidget.setResource(preferanserResources.h10());
                break;

            case SPADE_JACK:
                cardWidget.setResource(preferanserResources.sj());
                break;
            case CLUB_JACK:
                cardWidget.setResource(preferanserResources.cj());
                break;
            case DIAMOND_JACK:
                cardWidget.setResource(preferanserResources.dj());
                break;
            case HEART_JACK:
                cardWidget.setResource(preferanserResources.hj());
                break;

            case SPADE_QUEEN:
                cardWidget.setResource(preferanserResources.sq());
                break;
            case CLUB_QUEEN:
                cardWidget.setResource(preferanserResources.cq());
                break;
            case DIAMOND_QUEEN:
                cardWidget.setResource(preferanserResources.dq());
                break;
            case HEART_QUEEN:
                cardWidget.setResource(preferanserResources.hq());
                break;

            case SPADE_KING:
                cardWidget.setResource(preferanserResources.sk());
                break;
            case CLUB_KING:
                cardWidget.setResource(preferanserResources.ck());
                break;
            case DIAMOND_KING:
                cardWidget.setResource(preferanserResources.dk());
                break;
            case HEART_KING:
                cardWidget.setResource(preferanserResources.hk());
                break;

            case SPADE_ACE:
                cardWidget.setResource(preferanserResources.sa());
                break;
            case CLUB_ACE:
                cardWidget.setResource(preferanserResources.ca());
                break;
            case DIAMOND_ACE:
                cardWidget.setResource(preferanserResources.da());
                break;
            case HEART_ACE:
                cardWidget.setResource(preferanserResources.ha());
                break;
        }

        cardWidget.setHandlers(this);
        cardWidget.addStyleName(style.card());
        cardWidgetBiMap.put(card, cardWidget);
        return cardWidget;
    }

    private void populateLocationPanelMap() {
        locationPanelMap.put(NORTH, northPanel);
        locationPanelMap.put(EAST, eastPanel);
        locationPanelMap.put(SOUTH, southPanel);
        locationPanelMap.put(WEST, westPanel);
        locationPanelMap.put(CENTER, centerPanel);
    }

    private void populateLocationLayoutMap() {
        int cardWidth = preferanserResources.c7().getWidth();
        int cardHeight = preferanserResources.c7().getHeight();
        final CenterCardLayout centerCardLayout = new CenterCardLayout(centerPanel, cardWidth, cardHeight);
        centerCardLayout.setFirstTurn(Cardinal.NORTH);
        eventBus.addHandler(TurnChangeEvent.getType(), new TurnChangeEvent.TurnChangeEventHandler() {
            @Override public void onTurnChange(TurnChangeEvent event) {
                centerCardLayout.setFirstTurn(event.getCardinal());
            }
        });

        locationLayoutMap.put(NORTH, new HorizontalCardLayout(northPanel, cardWidth));
        locationLayoutMap.put(EAST, new EastCardLayout(eastPanel, cardWidth, cardHeight));
        locationLayoutMap.put(SOUTH, new HorizontalCardLayout(southPanel, cardWidth));
        locationLayoutMap.put(WEST, new WestCardLayout(westPanel, cardWidth, cardHeight));
        locationLayoutMap.put(CENTER, centerCardLayout);
    }

    private void populateCardinalTrickCounts() {
        cardinalTrickCount.put(Cardinal.NORTH, trickCountNorth);
        cardinalTrickCount.put(Cardinal.EAST, trickCountEast);
        cardinalTrickCount.put(Cardinal.SOUTH, trickCountSouth);
        cardinalTrickCount.put(Cardinal.WEST, trickCountWest);
    }

    private void populateCardinalTitles() {
        cardinalTitle.put(Cardinal.NORTH, titleNorth);
        cardinalTitle.put(Cardinal.EAST, titleEast);
        cardinalTitle.put(Cardinal.SOUTH, titleSouth);
        cardinalTitle.put(Cardinal.WEST, titleWest);
    }

}
