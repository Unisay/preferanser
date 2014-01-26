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

package com.preferanser.client.application.mvp;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.BiMap;
import com.google.common.collect.EnumHashBiMap;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.preferanser.client.application.i18n.I18nHelper;
import com.preferanser.client.application.i18n.PreferanserConstants;
import com.preferanser.client.application.mvp.editor.TableUiHandlers;
import com.preferanser.client.application.mvp.editor.style.TableStyle;
import com.preferanser.client.application.widgets.CardWidget;
import com.preferanser.client.application.widgets.HandCard;
import com.preferanser.client.application.widgets.TablePanel;
import com.preferanser.client.application.widgets.TurnPointer;
import com.preferanser.client.geom.Point;
import com.preferanser.client.geom.Rect;
import com.preferanser.laf.client.PreferanserResources;
import com.preferanser.shared.domain.*;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMapWithExpectedSize;

abstract public class BaseTableView<U extends TableUiHandlers> extends ViewWithUiHandlers<U> implements TableView, CardWidget.Handlers {

    protected final Map<Hand, Label> handTricksCountMap = newHashMapWithExpectedSize(Hand.values().length);

    protected final Map<Hand, TurnPointer> handTurnPointerMap = newHashMapWithExpectedSize(Hand.PLAYING_HANDS.size());
    protected final BiMap<Card, CardWidget> cardWidgetBiMap = EnumHashBiMap.create(Card.class);
    private final Map<Hand, Label> handTitleMap = newHashMapWithExpectedSize(Hand.PLAYING_HANDS.size());
    private final ImageDragController imageDragController;

    protected final I18nHelper i18nHelper;
    protected final CardImageResourceRetriever cardImageResourceRetriever;

    @UiField(provided = true) public PreferanserConstants constants;
    @UiField(provided = true) public PreferanserResources resources;

    @UiField public TableStyle style;
    @UiField public TablePanel table;
    @UiField public InlineHTML authLabel;
    @UiField public Hyperlink logout;

    @UiField public TurnPointer turnPointerEast;
    @UiField public TurnPointer turnPointerSouth;
    @UiField public TurnPointer turnPointerWest;

    @UiField public Label titleEast;
    @UiField public Label titleSouth;
    @UiField public Label titleWest;

    public BaseTableView(PreferanserConstants constants, PreferanserResources resources, I18nHelper i18nHelper) {
        this.constants = constants;
        this.i18nHelper = i18nHelper;
        this.resources = resources;
        cardImageResourceRetriever = new CardImageResourceRetriever(resources);
        imageDragController = new ImageDragController(Document.get(), resources.css().cardDragging());
    }

    protected void init() {
        installMouseUpHandler(RootPanel.get());
        installMouseUpHandler();
        installMouseMoveHandler(RootPanel.get());
        populateHandTitles();
        populateHandTurnPointers();
    }

    public void displayCards(Map<Hand, Set<Card>> handCards, Map<Card, Hand> centerCards, Widow widow) {
        detachCardWidgets();
        displayHandCards(handCards);
        displayCenterCards(centerCards);
        displayWidow(widow);
    }

    protected void detachCardWidgets() {
        for (CardWidget cardWidget : cardWidgetBiMap.values())
            cardWidget.removeFromParent();
    }

    private void displayHandCards(Map<Hand, Set<Card>> handCards) {
        for (Hand hand : Hand.PLAYING_HANDS)
            displayHandCards(hand, handCards.get(hand));
    }

    private void displayWidow(Widow widow) {
        Panel widowPanel = table.getLocationWidgetsContainer(TableLocation.WIDOW);
        for (Card card : widow)
            displayCard(widowPanel, card);
        table.layoutLocation(TableLocation.WIDOW);
    }

    private void displayCenterCards(Map<Card, Hand> centerCards) {
        Function<Map.Entry<Card, Hand>, HandCard> func = new Function<Map.Entry<Card, Hand>, HandCard>() {
            @Nullable
            @Override
            public HandCard apply(Map.Entry<Card, Hand> entry) {
                Card card = entry.getKey();
                CardWidget cardWidget = cardWidgetBiMap.get(card);
                if (null == cardWidget) {
                    cardWidget = createCardWidget(card);
                    cardWidgetBiMap.put(card, cardWidget);
                }
                return new HandCard(entry.getValue(), cardWidget);
            }
        };
        displayCenterCards(newArrayList(transform(centerCards.entrySet(), func)));
    }

    protected void displayCenterCards(List<HandCard> handCards) {
        table.addHandCardsToCenter(handCards);
    }

    private void displayHandCards(Hand hand, Iterable<Card> cards) {
        TableLocation location = TableLocation.valueOf(hand);
        HasWidgets panel = table.getLocationWidgetsContainer(location);
        for (Card card : cards)
            displayCard(panel, card);
        table.layoutLocation(location);
    }

    private void displayCard(HasWidgets panel, Card card) {
        CardWidget cardWidget;
        if (cardWidgetBiMap.containsKey(card)) {
            cardWidget = cardWidgetBiMap.get(card);
        } else {
            cardWidget = createCardWidget(card);
            cardWidgetBiMap.put(card, cardWidget);
        }
        displayCardWidget(panel, cardWidget);
    }

    protected void displayCardWidget(HasWidgets panel, CardWidget cardWidget) {
        panel.add(cardWidget);
    }

    public void displayContracts(Map<Hand, Contract> handContracts) {
        for (Hand hand : Hand.PLAYING_HANDS) {
            if (handContracts.containsKey(hand))
                displayHandContract(hand, handContracts.get(hand));
            else
                displayNoContract(hand);
        }
    }

    protected abstract void displayHandContract(Hand hand, Contract contract);

    protected abstract void displayNoContract(Hand hand);

    @Override
    public void displayAuthInfo(String authInfo) {
        authLabel.setHTML(authInfo);
    }

    @Override
    public void displayTurn(Hand turn) {
        for (Map.Entry<Hand, TurnPointer> entry : handTurnPointerMap.entrySet())
            displayHandTurnPointer(entry.getKey(), entry.getValue(), turn);
    }

    protected void displayHandTurnPointer(Hand hand, TurnPointer turnPointer, Hand turn) {
        turnPointer.setActive(hand == turn);
    }

    @Override
    public void onCardMouseDown(CardWidget cardWidget, MouseDownEvent event) {
        imageDragController.onCardWidgetMouseDown(cardWidget, event);
        if (!cardWidget.isDisabled())
            putCardImageOnTop(cardWidget);
    }

    @Override
    public void onCardDragStart(CardWidget cardWidget, DragStartEvent event) {
        event.stopPropagation();
        event.preventDefault();
    }

    @Override
    public void onCardDoubleClick(CardWidget cardWidget, DoubleClickEvent event) {
    }

    private void installMouseMoveHandler(RootPanel rootPanel) {
        rootPanel.addDomHandler(imageDragController, MouseMoveEvent.getType());
    }

    private void installMouseUpHandler(RootPanel rootPanel) {
        rootPanel.addDomHandler(imageDragController, MouseUpEvent.getType());
    }

    private void installMouseUpHandler() {
        final Map<Panel, TableLocation> panels = table.getPanelLocations();
        for (final Map.Entry<Panel, TableLocation> entry : panels.entrySet()) {
            final Panel sourcePanel = entry.getKey();
            sourcePanel.addDomHandler(new MouseUpHandler() {
                @Override public void onMouseUp(MouseUpEvent event) {
                    if (imageDragController.isDrag()) {
                        Point cursorPoint = Point.FromMouseEvent(event);
                        Card card = cardWidgetBiMap.inverse().get(imageDragController.getCardWidget());
                        getUiHandlers().changeCardLocation(card, findTargetTableLocation(cursorPoint));
                    }
                }
            }, MouseUpEvent.getType());
        }
    }

    private Optional<TableLocation> findTargetTableLocation(Point cursorPoint) {
        Map<Panel, TableLocation> panels = table.getPanelLocations();
        for (Panel targetPanel : panels.keySet()) {
            if (Rect.FromWidget(targetPanel).contains(cursorPoint)) {
                TableLocation newLocation = panels.get(targetPanel);
                return Optional.of(newLocation);
            }
        }
        return Optional.absent();
    }

    private void putCardImageOnTop(Widget image) {
        image.getElement().getStyle().setZIndex(getMaxCardZIndex() + 1);
    }

    private int getMaxCardZIndex() {
        int maxZIndex = 0;
        for (CardWidget cardWidget : cardWidgetBiMap.values()) {
            int zIndex;
            try {
                zIndex = Integer.parseInt(cardWidget.getElement().getStyle().getZIndex());
            } catch (NumberFormatException e) {
                zIndex = 0;
            }
            if (maxZIndex < zIndex)
                maxZIndex = zIndex;
        }
        return maxZIndex;
    }

    protected CardWidget createCardWidget(Card card) {
        CardWidget cardWidget = new CardWidget(card, resources.css().cardDisabled(), resources.css().cardDraggable());
        cardWidget.setResource(cardImageResourceRetriever.getByCard(card));
        cardWidget.setHandlers(this);
        cardWidget.addStyleName(resources.css().card());
        cardWidget.ensureDebugId(card.name());
        return cardWidget;
    }

    private void populateHandTitles() {
        handTitleMap.put(Hand.EAST, titleEast);
        handTitleMap.put(Hand.SOUTH, titleSouth);
        handTitleMap.put(Hand.WEST, titleWest);
    }

    protected void populateHandTurnPointers() {
        handTurnPointerMap.put(Hand.EAST, turnPointerEast);
        handTurnPointerMap.put(Hand.SOUTH, turnPointerSouth);
        handTurnPointerMap.put(Hand.WEST, turnPointerWest);
    }

    abstract protected Logger getLog();

    @UiHandler("logout") public void onLogoutClicked(@SuppressWarnings("unused") ClickEvent event) {
        // TODO implement AuthViewPresenter
    }

    @UiFactory public TurnPointer turnPointer() {
        return new TurnPointer(style, resources.arrowRight());
    }

    @UiFactory public TablePanel tablePanel() {
        return new TablePanel();
    }
}
