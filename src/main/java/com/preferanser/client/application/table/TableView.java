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
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.preferanser.client.application.event.TurnChangeEvent;
import com.preferanser.client.application.i18n.PreferanserConstants;
import com.preferanser.client.application.table.layout.*;
import com.preferanser.client.application.widgets.CardWidget;
import com.preferanser.client.application.widgets.ContractLink;
import com.preferanser.client.geom.Point;
import com.preferanser.client.geom.Rect;
import com.preferanser.client.theme.greencloth.client.com.preferanser.client.application.PreferanserResources;
import com.preferanser.shared.Card;
import com.preferanser.shared.Cardinal;
import com.preferanser.shared.Contract;
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

    private final GQuerySelectors selectors;
    private final EventBus eventBus;
    private final BiMap<Card, CardWidget> cardWidgetBiMap = EnumHashBiMap.create(Card.class);
    private final BiMap<TableLocation, FlowPanel> locationPanelMap = EnumHashBiMap.create(TableLocation.class);
    private final BiMap<TableLocation, CardLayout> locationLayoutMap = EnumHashBiMap.create(TableLocation.class);
    private final Map<Cardinal, Label> cardinalTricksCountMap = Maps.newHashMapWithExpectedSize(Cardinal.values().length);
    private final Map<Cardinal, Label> cardinalTitleMap = Maps.newHashMapWithExpectedSize(Cardinal.values().length);
    private final Map<Cardinal, ContractLink> cardinalContractMap = Maps.newHashMapWithExpectedSize(Cardinal.values().length);
    private final ImageDragController imageDragController = new ImageDragController(Document.get());

    @UiField PreferanserConstants constants;
    @UiField PreferanserResources resources;

    @UiField TableStyle style;
    @UiField Button dealButton;
    @UiField Button saveButton;
    @UiField ToggleButton playButton;
    @UiField ToggleButton editButton;


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

    @UiField ContractLink northContractLink;
    @UiField ContractLink eastContractLink;
    @UiField ContractLink southContractLink;
    @UiField ContractLink westContractLink;

    @Inject
    public TableView(Binder uiBinder, GQuerySelectors selectors, EventBus eventBus) {
        this.selectors = selectors;
        this.eventBus = eventBus;
        initWidget(uiBinder.createAndBindUi(this));
        disableStandardDragging(selectors.getAllDivsAndImages().elements());
        populateLocationPanelMap();
        populateLocationLayoutMap();
        populateCardinalTrickCounts();
        populateCardinalTitles();
        populateCardinalContractLinks();
        RootPanel rootPanel = RootPanel.get();
        installCenterPanelClickHandler();
        installMouseUpHandler(rootPanel);
        installMouseUpHandler(locationPanelMap.values());
        installMouseMoveHandler(rootPanel);
    }

    @Override
    public void displayTableCards(Map<TableLocation, Collection<Card>> tableCards) {
        for (CardWidget cardWidget : cardWidgetBiMap.values()) {
            cardWidget.removeFromParent();
        }
        for (Map.Entry<TableLocation, Collection<Card>> entry : tableCards.entrySet()) {
            displayCards(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void displayCardinalTricks(Map<Cardinal, Integer> cardinalTricks) {
        for (Map.Entry<Cardinal, Integer> entry : cardinalTricks.entrySet()) {
            cardinalTricksCountMap.get(entry.getKey()).setText("" + entry.getValue());
        }
    }

    @Override
    public void displayContracts(Map<Cardinal, Contract> cardinalContracts) {
        for (Map.Entry<Cardinal, Contract> entry : cardinalContracts.entrySet()) {
            Cardinal cardinal = entry.getKey();
            Contract contract = entry.getValue();
            ContractLink contractLink = cardinalContractMap.get(cardinal);
            contractLink.setContract(contract);
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

    @Override public void setPlayMode() {
        playButton.setDown(true);
        editButton.setDown(false);
        dealButton.setVisible(false);
        saveButton.setVisible(false);
        for (ContractLink contractLink : cardinalContractMap.values()) {
            contractLink.disable();
        }
    }

    @Override public void setEditMode() {
        editButton.setDown(true);
        playButton.setDown(false);
        dealButton.setVisible(true);
        saveButton.setVisible(true);
        for (ContractLink contractLink : cardinalContractMap.values()) {
            contractLink.enable();
        }
    }

    @UiHandler("playButton") void onPlayButtonClicked(@SuppressWarnings("unused") ClickEvent event) {
        getUiHandlers().setPlayMode();
    }

    @UiHandler("editButton") void onEditButtonClicked(@SuppressWarnings("unused") ClickEvent event) {
        getUiHandlers().setEditMode();
    }

    @UiHandler("dealButton") void onDealButtonClicked(@SuppressWarnings("unused") ClickEvent event) {
        getUiHandlers().dealCards();
    }

    @UiHandler("sluffLink") void onSluffLinkClicked(@SuppressWarnings("unused") ClickEvent event) {
        getUiHandlers().sluff();
    }

    @UiHandler("northContractLink") void onNorthContractLinkClicked(@SuppressWarnings("unused") ClickEvent event) {
        getUiHandlers().chooseContract(Cardinal.NORTH);
    }

    @UiHandler("eastContractLink") void onEastContractLinkClicked(@SuppressWarnings("unused") ClickEvent event) {
        getUiHandlers().chooseContract(Cardinal.EAST);
    }

    @UiHandler("southContractLink") void onSouthContractLinkClicked(@SuppressWarnings("unused") ClickEvent event) {
        getUiHandlers().chooseContract(Cardinal.SOUTH);
    }

    @UiHandler("westContractLink") void onWestContractLinkClicked(@SuppressWarnings("unused") ClickEvent event) {
        getUiHandlers().chooseContract(Cardinal.WEST);
    }

    @UiFactory ToggleButton toggleButton(String caption) {
        return new ToggleButton(caption, caption);
    }

    @UiFactory CardWidget createCardWidget(Card card) {
        CardWidget cardWidget = new CardWidget(card);

        switch (card) {
            case SPADE_SEVEN:
                cardWidget.setResource(resources.s7());
                break;
            case CLUB_SEVEN:
                cardWidget.setResource(resources.c7());
                break;
            case DIAMOND_SEVEN:
                cardWidget.setResource(resources.d7());
                break;
            case HEART_SEVEN:
                cardWidget.setResource(resources.h7());
                break;

            case SPADE_EIGHT:
                cardWidget.setResource(resources.s8());
                break;
            case CLUB_EIGHT:
                cardWidget.setResource(resources.c8());
                break;
            case DIAMOND_EIGHT:
                cardWidget.setResource(resources.d8());
                break;
            case HEART_EIGHT:
                cardWidget.setResource(resources.h8());
                break;

            case SPADE_NINE:
                cardWidget.setResource(resources.s9());
                break;
            case CLUB_NINE:
                cardWidget.setResource(resources.c9());
                break;
            case DIAMOND_NINE:
                cardWidget.setResource(resources.d9());
                break;
            case HEART_NINE:
                cardWidget.setResource(resources.h9());
                break;

            case SPADE_TEN:
                cardWidget.setResource(resources.s10());
                break;
            case CLUB_TEN:
                cardWidget.setResource(resources.c10());
                break;
            case DIAMOND_TEN:
                cardWidget.setResource(resources.d10());
                break;
            case HEART_TEN:
                cardWidget.setResource(resources.h10());
                break;

            case SPADE_JACK:
                cardWidget.setResource(resources.sj());
                break;
            case CLUB_JACK:
                cardWidget.setResource(resources.cj());
                break;
            case DIAMOND_JACK:
                cardWidget.setResource(resources.dj());
                break;
            case HEART_JACK:
                cardWidget.setResource(resources.hj());
                break;

            case SPADE_QUEEN:
                cardWidget.setResource(resources.sq());
                break;
            case CLUB_QUEEN:
                cardWidget.setResource(resources.cq());
                break;
            case DIAMOND_QUEEN:
                cardWidget.setResource(resources.dq());
                break;
            case HEART_QUEEN:
                cardWidget.setResource(resources.hq());
                break;

            case SPADE_KING:
                cardWidget.setResource(resources.sk());
                break;
            case CLUB_KING:
                cardWidget.setResource(resources.ck());
                break;
            case DIAMOND_KING:
                cardWidget.setResource(resources.dk());
                break;
            case HEART_KING:
                cardWidget.setResource(resources.hk());
                break;

            case SPADE_ACE:
                cardWidget.setResource(resources.sa());
                break;
            case CLUB_ACE:
                cardWidget.setResource(resources.ca());
                break;
            case DIAMOND_ACE:
                cardWidget.setResource(resources.da());
                break;
            case HEART_ACE:
                cardWidget.setResource(resources.ha());
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
        int cardWidth = resources.c7().getWidth();
        int cardHeight = resources.c7().getHeight();
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
        cardinalTricksCountMap.put(Cardinal.NORTH, trickCountNorth);
        cardinalTricksCountMap.put(Cardinal.EAST, trickCountEast);
        cardinalTricksCountMap.put(Cardinal.SOUTH, trickCountSouth);
        cardinalTricksCountMap.put(Cardinal.WEST, trickCountWest);
    }

    private void populateCardinalTitles() {
        cardinalTitleMap.put(Cardinal.NORTH, titleNorth);
        cardinalTitleMap.put(Cardinal.EAST, titleEast);
        cardinalTitleMap.put(Cardinal.SOUTH, titleSouth);
        cardinalTitleMap.put(Cardinal.WEST, titleWest);
    }

    private void populateCardinalContractLinks() {
        cardinalContractMap.put(Cardinal.NORTH, northContractLink);
        cardinalContractMap.put(Cardinal.EAST, eastContractLink);
        cardinalContractMap.put(Cardinal.SOUTH, southContractLink);
        cardinalContractMap.put(Cardinal.WEST, westContractLink);
    }

}
