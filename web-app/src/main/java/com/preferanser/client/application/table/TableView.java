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
import com.google.gwt.event.dom.client.*;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.preferanser.client.application.i18n.PreferanserConstants;
import com.preferanser.client.application.table.layout.*;
import com.preferanser.client.application.table.style.TableStyle;
import com.preferanser.client.application.widgets.CardWidget;
import com.preferanser.client.application.widgets.CardinalCard;
import com.preferanser.client.application.widgets.ContractLink;
import com.preferanser.client.application.widgets.TurnPointer;
import com.preferanser.client.geom.Point;
import com.preferanser.client.geom.Rect;
import com.preferanser.client.theme.greencloth.client.com.preferanser.client.application.PreferanserResources;
import com.preferanser.domain.Card;
import com.preferanser.domain.Cardinal;
import com.preferanser.domain.Contract;
import com.preferanser.domain.TableLocation;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;
import java.util.logging.Logger;

import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMapWithExpectedSize;
import static com.preferanser.domain.TableLocation.*;

/**
 * Table view
 */
public class TableView extends ViewWithUiHandlers<TableUiHandlers> implements TablePresenter.TableView, CardWidget.Handlers {

    private static final Logger log = Logger.getLogger("TableView");

    public interface Binder extends UiBinder<Widget, TableView> {}

    private final BiMap<Card, CardWidget> cardWidgetBiMap = EnumHashBiMap.create(Card.class);
    private final BiMap<TableLocation, FlowPanel> locationPanelMap = EnumHashBiMap.create(TableLocation.class);
    private CenterLayout centerCardLayout;
    private final BiMap<TableLocation, Layout<CardWidget>> locationLayoutMap = EnumHashBiMap.create(TableLocation.class);
    private final Map<Cardinal, Label> cardinalTricksCountMap = newHashMapWithExpectedSize(Cardinal.values().length);
    private final Map<Cardinal, Label> cardinalTitleMap = newHashMapWithExpectedSize(Cardinal.values().length);
    private final Map<Cardinal, ContractLink> cardinalContractMap = newHashMapWithExpectedSize(Cardinal.values().length);
    private final Map<Cardinal, TurnPointer> cardinalTurnPointerMap = newHashMapWithExpectedSize(Cardinal.values().length);
    private final ImageDragController imageDragController = new ImageDragController(Document.get());
    private PreferanserResources resources;
    private CardImageResourceRetriever cardImageResourceRetriever;

    @UiField(provided = true) PreferanserConstants constants;

    @UiField TableStyle style;
    @UiField Button resetButton;
    @UiField Button saveButton;
    @UiField(provided = true) ToggleButton playButton;
    @UiField(provided = true) ToggleButton editButton;

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

    @UiField TurnPointer turnPointerNorth;
    @UiField TurnPointer turnPointerEast;
    @UiField TurnPointer turnPointerSouth;
    @UiField TurnPointer turnPointerWest;

    private boolean editMode;

    @Inject
    public TableView(Binder uiBinder, PreferanserResources resources, PreferanserConstants constants) {
        this.resources = resources;
        this.constants = constants;
        cardImageResourceRetriever = new CardImageResourceRetriever(resources);
        playButton = new ToggleButton(constants.play(), constants.play());
        editButton = new ToggleButton(constants.edit(), constants.edit());

        initWidget(uiBinder.createAndBindUi(this));

        populateLocationPanelMap();
        populateLocationLayoutMap();
        populateCardinalTrickCounts();
        populateCardinalTitles();
        populateCardinalContractLinks();
        populateCardinalTurnPointers();
        RootPanel rootPanel = RootPanel.get();
        installCenterPanelClickHandler();
        installMouseUpHandler(rootPanel);
        installMouseUpHandler(locationPanelMap.values());
        installMouseMoveHandler(rootPanel);
    }

    @Override
    public TableView displayTableCards(Map<TableLocation, Collection<Card>> tableCards, Map<Card, Cardinal> centerCards) {
        for (CardWidget cardWidget : cardWidgetBiMap.values()) {
            cardWidget.removeFromParent();
        }

        for (Map.Entry<TableLocation, Collection<Card>> entry : tableCards.entrySet()) {
            displayCards(entry.getKey(), entry.getValue());
        }
        return displayCenterCards(centerCards);
    }

    private TableView displayCenterCards(Map<Card, Cardinal> centerCards) {
        Function<Map.Entry<Card, Cardinal>, CardinalCard> func = new Function<Map.Entry<Card, Cardinal>, CardinalCard>() {
            @Nullable @Override public CardinalCard apply(Map.Entry<Card, Cardinal> entry) {
                Card card = entry.getKey();
                CardWidget cardWidget = cardWidgetBiMap.get(card);
                if (null == cardWidget) {
                    cardWidget = createCardWidget(card);
                    cardWidgetBiMap.put(card, cardWidget);
                }
                return new CardinalCard(entry.getValue(), cardWidget);
            }
        };
        Collection<CardinalCard> widgets = newArrayList(transform(centerCards.entrySet(), func));
        for (CardinalCard cardinalCard : widgets)
            centerPanel.add(cardinalCard.getCardWidget());
        centerCardLayout.apply(widgets);
        return this;
    }

    private TableView displayCards(TableLocation location, Iterable<Card> cards) {
        HasWidgets panel = locationPanelMap.get(location);
        for (Card card : cards) {
            displayCard(panel, card);
        }
        layoutLocation(location);
        return this;
    }

    private void displayCard(HasWidgets panel, Card card) {
        CardWidget cardWidget;
        if (cardWidgetBiMap.containsKey(card)) {
            cardWidget = cardWidgetBiMap.get(card);
        } else {
            cardWidget = createCardWidget(card);
            cardWidgetBiMap.put(card, cardWidget);
        }
        panel.add(cardWidget);
    }

    @Override
    public TableView displayCardinalTricks(Map<Cardinal, Integer> cardinalTricks) {
        for (Map.Entry<Cardinal, Integer> entry : cardinalTricks.entrySet()) {
            cardinalTricksCountMap.get(entry.getKey()).setText("" + entry.getValue());
        }
        return this;
    }

    @Override
    public TableView hideCardinalTricks() {
        for (Map.Entry<Cardinal, Label> entry : cardinalTricksCountMap.entrySet()) {
            entry.getValue().setText("");
        }
        return this;
    }

    @Override
    public TableView displayContracts(Map<Cardinal, Contract> cardinalContracts) {
        for (Cardinal cardinal : Cardinal.values()) {
            ContractLink contractLink = cardinalContractMap.get(cardinal);
            if (cardinalContracts.containsKey(cardinal)) {
                Contract contract = cardinalContracts.get(cardinal);
                contractLink.setContract(contract);
            } else {
                contractLink.setContract(null);
            }
        }
        return this;
    }

    @Override
    public TableView displayTurn(Cardinal turn) {
        for (Map.Entry<Cardinal, TurnPointer> entry : cardinalTurnPointerMap.entrySet()) {
            Cardinal cardinal = entry.getKey();
            TurnPointer turnPointer = entry.getValue();
            turnPointer.setActive(cardinal == turn);
            if (!editMode) {
                if (turnPointer.isActive()) {
                    turnPointer.removeStyleName(style.notDisplayed());
                } else {
                    turnPointer.addStyleName(style.notDisplayed());
                }
            }
        }
        return this;
    }

    @Override
    public TableView hideTurn() {
        if (!editMode)
            for (TurnPointer turnPointer : cardinalTurnPointerMap.values())
                turnPointer.addStyleName(style.notDisplayed());

        return this;
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

    private void layoutLocation(TableLocation location) {
        FlowPanel panel = locationPanelMap.get(location);
        Layout<CardWidget> layout = locationLayoutMap.get(location);
        Collection<CardWidget> cardWidgets = newArrayList(transform(panel, new Function<Widget, CardWidget>() {
            @Nullable @Override public CardWidget apply(@Nullable Widget widget) {
                if (widget instanceof CardWidget) {
                    return (CardWidget) widget;
                }
                return null;
            }
        }));
        layout.apply(cardWidgets);
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

    @Override
    public TableView setPlayMode() {
        editMode = false;
        playButton.setDown(true);
        editButton.setDown(false);
        resetButton.setVisible(false);
        saveButton.setVisible(false);
        for (ContractLink contractLink : cardinalContractMap.values()) {
            contractLink.disable();
        }
        for (TurnPointer turnPointer : cardinalTurnPointerMap.values()) {
            if (!turnPointer.isActive()) {
                turnPointer.addStyleName(style.notDisplayed());
            }
        }
        return this;
    }

    @Override
    public TableView setEditMode() {
        editMode = true;
        editButton.setDown(true);
        playButton.setDown(false);
        resetButton.setVisible(true);
        saveButton.setVisible(true);
        for (ContractLink contractLink : cardinalContractMap.values()) {
            contractLink.enable();
        }
        for (TurnPointer turnPointer : cardinalTurnPointerMap.values()) {
            turnPointer.removeStyleName(style.notDisplayed());
        }
        return this;
    }

    @UiHandler("playButton") void onPlayButtonClicked(@SuppressWarnings("unused") ClickEvent event) {
        getUiHandlers().setPlayMode();
    }

    @UiHandler("editButton") void onEditButtonClicked(@SuppressWarnings("unused") ClickEvent event) {
        getUiHandlers().setEditMode();
    }

    @UiHandler("resetButton") void onResetButtonClicked(@SuppressWarnings("unused") ClickEvent event) {
        getUiHandlers().reset();
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

    @UiFactory TurnPointer turnPointer() {
        final TurnPointer turnPointer = new TurnPointer(style, resources.arrowRight());
        turnPointer.addClickHandler(new ClickHandler() {
            @Override public void onClick(ClickEvent event) {
                getUiHandlers().chooseTurn(turnPointer.getTurn());
            }
        });
        return turnPointer;
    }

    private CardWidget createCardWidget(Card card) {
        CardWidget cardWidget = new CardWidget(card);
        cardWidget.setResource(cardImageResourceRetriever.getByCard(card));
        cardWidget.setHandlers(this);
        cardWidget.addStyleName(style.card());
        cardWidget.ensureDebugId(card.name());
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
        centerCardLayout = new CenterLayout(centerPanel, cardWidth, cardHeight);
        locationLayoutMap.put(NORTH, new HorizontalLayout(northPanel, cardWidth));
        locationLayoutMap.put(EAST, new EastLayout(eastPanel, cardWidth, cardHeight));
        locationLayoutMap.put(SOUTH, new HorizontalLayout(southPanel, cardWidth));
        locationLayoutMap.put(WEST, new WestLayout(westPanel, cardWidth, cardHeight));
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

    private void populateCardinalTurnPointers() {
        cardinalTurnPointerMap.put(Cardinal.NORTH, turnPointerNorth);
        cardinalTurnPointerMap.put(Cardinal.EAST, turnPointerEast);
        cardinalTurnPointerMap.put(Cardinal.SOUTH, turnPointerSouth);
        cardinalTurnPointerMap.put(Cardinal.WEST, turnPointerWest);
    }

}
