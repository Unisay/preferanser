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

package com.preferanser.client.application.mvp.player;

import com.google.common.base.Optional;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import com.preferanser.client.application.i18n.I18nHelper;
import com.preferanser.client.application.i18n.PreferanserConstants;
import com.preferanser.client.application.mvp.BaseTableView;
import com.preferanser.client.application.widgets.CardWidget;
import com.preferanser.client.application.widgets.HandCard;
import com.preferanser.client.application.widgets.TurnPointer;
import com.preferanser.laf.client.PreferanserResources;
import com.preferanser.shared.domain.Card;
import com.preferanser.shared.domain.Contract;
import com.preferanser.shared.domain.Hand;
import com.preferanser.shared.domain.TableLocation;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

public class PlayerViewImpl extends BaseTableView<PlayerUiHandlers> implements PlayerView {

    private static final Logger log = Logger.getLogger("PlayerView");

    public interface Binder extends UiBinder<Widget, PlayerViewImpl> {}

    @UiField Button editButton;
    @UiField Button resetButton;
    @UiField Button drawingsButton;
    @UiField Button saveDrawingButton;
    @UiField Label trickCountEast;
    @UiField Label trickCountSouth;
    @UiField Label trickCountWest;
    @UiField Anchor undoAnchor;
    @UiField Anchor redoAnchor;
    @UiField Button sluffButton;
    @UiField Button closeButton;
    @UiField Button turnWidowButton;
    @UiField TurnPointer turnPointerWidow;
    @UiField Label dealName;
    @UiField InlineLabel dealDescription;

    @Inject
    public PlayerViewImpl(Binder uiBinder, PreferanserResources resources, PreferanserConstants constants, I18nHelper i18nHelper) {
        super(constants, resources, i18nHelper);
        initWidget(uiBinder.createAndBindUi(this));
        init();
    }

    @Override protected void init() {
        super.init();
        populateHandTrickCounts();
    }

    @Override public void disableCards(Set<Card> cards) {
        for (Map.Entry<Card, CardWidget> entry : cardWidgetBiMap.entrySet()) {
            CardWidget cardWidget = entry.getValue();
            boolean disabled = cards.contains(entry.getKey());
            cardWidget.setDisabled(disabled);
            if (disabled)
                cardWidget.setDraggable(false);
        }
    }

    @Override protected void displayCenterCards(List<HandCard> handCards) {
        for (HandCard handCard : handCards)
            handCard.getCardWidget().setDraggable(false);
        super.displayCenterCards(handCards);
    }

    @Override protected void displayCardWidget(HasWidgets panel, CardWidget cardWidget) {
        cardWidget.setDraggable(true);
        super.displayCardWidget(panel, cardWidget);
    }

    @Override public void displayTurnNavigation(boolean showPrev, boolean showNext) {
        undoAnchor.setVisible(showPrev);
        redoAnchor.setVisible(showNext);
    }

    @Override public void displayResetButton(boolean visible) {
        resetButton.setVisible(visible);
    }

    @Override public void displaySluffButton(boolean visible) {
        sluffButton.setVisible(visible);
    }

    @Override public void displayDrawingsButton(boolean visible) {
        drawingsButton.setVisible(visible);
    }

    @Override public void displaySaveDrawingButton(boolean visible) {
        saveDrawingButton.setVisible(visible);
    }

    @Override public void switchTabGame() {
        table.switchToTab(1);
    }

    @Override protected void displayHandTurnPointer(Hand hand, TurnPointer turnPointer, Hand turn) {
        super.displayHandTurnPointer(hand, turnPointer, turn);
        if (turnPointer.isActive()) {
            turnPointer.removeStyleName(style.notDisplayed());
        } else {
            turnPointer.addStyleName(style.notDisplayed());
        }
        if (hand == Hand.WIDOW) {
            turnWidowButton.setVisible(turnPointer.isActive());
        }
    }

    @UiHandler("closeButton") void onCloseClicked(@SuppressWarnings("unused") ClickEvent event) {
        getUiHandlers().close();
    }

    @UiHandler("editButton") void onEditClicked(@SuppressWarnings("unused") ClickEvent event) {
        getUiHandlers().switchToEditor();
    }

    @UiHandler("resetButton") void onResetClicked(@SuppressWarnings("unused") ClickEvent event) {
        getUiHandlers().reset();
    }

    @UiHandler("undoAnchor") void onUndoClicked(@SuppressWarnings("unused") ClickEvent event) {
        getUiHandlers().undo();
    }

    @UiHandler("redoAnchor") void onRedoClicked(@SuppressWarnings("unused") ClickEvent event) {
        getUiHandlers().redo();
    }

    @UiHandler("sluffButton") void onSluffClicked(@SuppressWarnings("unused") ClickEvent event) {
        getUiHandlers().sluff();
    }

    @UiHandler("turnWidowButton") void onTurnFromWidowClicked(@SuppressWarnings("unused") ClickEvent event) {
        getUiHandlers().turnFromWidow();
    }

    @UiHandler("saveDrawingButton") void onSaveDrawing(@SuppressWarnings("unused") ClickEvent event) {
        getUiHandlers().saveDrawing();
    }

    @UiHandler("drawingsButton") void onDrawings(@SuppressWarnings("unused") ClickEvent event) {
        getUiHandlers().openDrawings();
    }

    @Override public void onCardDoubleClick(CardWidget cardWidget, DoubleClickEvent event) {
        if (cardWidget.isEnabled() && cardWidget.isDraggable()) {
            getLog().finer("Card location change: " + cardWidget.getCard() + " -> " + TableLocation.CENTER);
            getUiHandlers().changeCardLocation(cardWidget.getCard(), Optional.of(TableLocation.CENTER));
        }
    }

    @Override protected void populateHandTurnPointers() {
        super.populateHandTurnPointers();
        handTurnPointerMap.put(Hand.WIDOW, turnPointerWidow);
    }

    @Override public void displayDealInfo(String name, String description) {
        dealName.setText(name);
        dealDescription.setText(description);
    }

    @Override public void displayHandTricks(Map<Hand, Integer> handTricks) {
        for (Hand hand : Hand.PLAYING_HANDS)
            handTricksCountMap.get(hand).setText(handTricks.get(hand).toString());
    }

    @Override protected void displayHandContract(Hand hand, Contract contract) {
        Label label = getHandContractTextHolder(hand);
        label.setText(constants.getString(hand.name()) + " – " + i18nHelper.getContractName(contract).toLowerCase());
        label.setVisible(true);
    }

    @Override protected void displayNoContract(Hand hand) {
        getHandContractTextHolder(hand).setVisible(false);
    }

    private Label getHandContractTextHolder(Hand hand) {
        switch (hand) {
            case EAST:
                return titleEast;
            case SOUTH:
                return titleSouth;
            case WEST:
                return titleWest;
            default:
                throw new IllegalStateException("No contract label for the hand: " + hand);
        }
    }

    private void populateHandTrickCounts() {
        handTricksCountMap.put(Hand.EAST, trickCountEast);
        handTricksCountMap.put(Hand.SOUTH, trickCountSouth);
        handTricksCountMap.put(Hand.WEST, trickCountWest);
    }

    private Logger getLog() {
        return log;
    }
}
