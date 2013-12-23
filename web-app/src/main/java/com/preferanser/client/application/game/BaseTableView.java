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

package com.preferanser.client.application.game;

import com.google.common.base.Function;
import com.google.common.collect.BiMap;
import com.google.common.collect.EnumHashBiMap;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.preferanser.client.application.game.editor.style.TableStyle;
import com.preferanser.client.application.i18n.I18nHelper;
import com.preferanser.client.application.i18n.PreferanserConstants;
import com.preferanser.client.application.widgets.CardWidget;
import com.preferanser.client.application.widgets.CardinalCard;
import com.preferanser.client.application.widgets.TablePanel;
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

abstract public class BaseTableView<U extends TableUiHandlers> extends ViewWithUiHandlers<U> implements TableView, CardWidget.Handlers {

    protected final Map<Cardinal, Label> cardinalTricksCountMap = newHashMapWithExpectedSize(Cardinal.values().length);

    public final Map<Cardinal, TurnPointer> cardinalTurnPointerMap = newHashMapWithExpectedSize(Cardinal.values().length);
    private final BiMap<Card, CardWidget> cardWidgetBiMap = EnumHashBiMap.create(Card.class);
    private final Map<Cardinal, Label> cardinalTitleMap = newHashMapWithExpectedSize(Cardinal.values().length);
    private final ImageDragController imageDragController = new ImageDragController(Document.get());

    protected final I18nHelper i18nHelper;
    protected final PreferanserResources resources;
    protected final CardImageResourceRetriever cardImageResourceRetriever;

    @UiField(provided = true) public PreferanserConstants constants;
    @UiField public TableStyle tableStyle;
    @UiField public TablePanel table;

    @UiField public TurnPointer turnPointerNorth;
    @UiField public TurnPointer turnPointerEast;
    @UiField public TurnPointer turnPointerSouth;
    @UiField public TurnPointer turnPointerWest;

    @UiField public Label trickCountNorth;
    @UiField public Label trickCountEast;
    @UiField public Label trickCountSouth;
    @UiField public Label trickCountWest;

    @UiField public Label titleNorth;
    @UiField public Label titleEast;
    @UiField public Label titleSouth;
    @UiField public Label titleWest;

    public BaseTableView(PreferanserConstants constants, PreferanserResources resources, I18nHelper i18nHelper) {
        cardImageResourceRetriever = new CardImageResourceRetriever(resources);
        this.constants = constants;
        this.i18nHelper = i18nHelper;
        this.resources = resources;
    }

    protected void init() {
        installMouseUpHandler(RootPanel.get());
        installMouseUpHandler(table.locationPanelMap.values());
        installMouseMoveHandler(RootPanel.get());
        populateCardinalTrickCounts();
        populateCardinalTitles();
        populateCardinalTurnPointers();
    }

    public void displayTableCards(Map<TableLocation, Collection<Card>> tableCards, Map<Card, Cardinal> centerCards) {
        for (CardWidget cardWidget : cardWidgetBiMap.values()) {
            cardWidget.removeFromParent();
        }

        for (Map.Entry<TableLocation, Collection<Card>> entry : tableCards.entrySet()) {
            displayCards(entry.getKey(), entry.getValue());
        }
        displayCenterCards(centerCards);
    }

    private void displayCenterCards(Map<Card, Cardinal> centerCards) {
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
        table.addCardinalCardsToCenter(newArrayList(transform(centerCards.entrySet(), func)));
    }

    private void displayCards(TableLocation location, Iterable<Card> cards) {
        HasWidgets panel = table.locationPanelMap.get(location);
        for (Card card : cards) {
            displayCard(panel, card);
        }
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
        panel.add(cardWidget);
    }

    public void displayContracts(Map<Cardinal, Contract> cardinalContracts) {
        for (Cardinal cardinal : Cardinal.values()) {
            HasText hasContractText = getCardinalContractTextHolder(cardinal);
            if (cardinalContracts.containsKey(cardinal)) {
                Contract contract = cardinalContracts.get(cardinal);
                hasContractText.setText(i18nHelper.getContractName(contract));
            } else {
                hasContractText.setText("");
            }
        }
    }

    protected abstract HasText getCardinalContractTextHolder(Cardinal cardinal);

    public void displayCardinalTricks(Map<Cardinal, Integer> cardinalTricks) {
        for (Map.Entry<Cardinal, Integer> entry : cardinalTricks.entrySet()) {
            cardinalTricksCountMap.get(entry.getKey()).setText("" + entry.getValue());
        }
    }

    public void displayTurn(Cardinal turn) {
        for (Map.Entry<Cardinal, TurnPointer> entry : cardinalTurnPointerMap.entrySet())
            displayCardinalTurnPointer(entry.getKey(), entry.getValue(), turn);
    }

    protected void displayCardinalTurnPointer(Cardinal cardinal, TurnPointer turnPointer, Cardinal turn) {
        turnPointer.setActive(cardinal == turn);
    }

    public void onCardMouseDown(CardWidget cardWidget, MouseDownEvent event) {
        imageDragController.startDrag(cardWidget, event);
        putCardImageOnTop(cardWidget);
    }

    public void onCardDragStart(CardWidget cardWidget, DragStartEvent event) {
        event.stopPropagation();
        event.preventDefault();
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
                                TableLocation oldLocation = table.locationPanelMap.inverse().get(sourcePanel);
                                TableLocation newLocation = table.locationPanelMap.inverse().get(targetPanel);
                                getLog().finer("Card newLocation change: " + card + ": " + oldLocation + " -> " + newLocation);
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

    private CardWidget createCardWidget(Card card) {
        CardWidget cardWidget = new CardWidget(card);
        cardWidget.setResource(cardImageResourceRetriever.getByCard(card));
        cardWidget.setHandlers(this);
        cardWidget.addStyleName(tableStyle.card());
        cardWidget.ensureDebugId(card.name());
        return cardWidget;
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

    protected void populateCardinalTurnPointers() {
        cardinalTurnPointerMap.put(Cardinal.NORTH, turnPointerNorth);
        cardinalTurnPointerMap.put(Cardinal.EAST, turnPointerEast);
        cardinalTurnPointerMap.put(Cardinal.SOUTH, turnPointerSouth);
        cardinalTurnPointerMap.put(Cardinal.WEST, turnPointerWest);
    }

    abstract protected Logger getLog();


    public @UiFactory TurnPointer turnPointer() {
        return new TurnPointer(tableStyle, resources.arrowRight());
    }

    public @UiFactory TablePanel tablePanel() {
        return new TablePanel(tableStyle);
    }
}
