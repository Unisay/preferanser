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

package com.preferanser.client.application.mvp.editor;

import com.google.common.base.Optional;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import com.preferanser.client.application.i18n.I18nHelper;
import com.preferanser.client.application.i18n.PreferanserConstants;
import com.preferanser.client.application.mvp.BaseTableView;
import com.preferanser.client.application.widgets.CardWidget;
import com.preferanser.client.application.widgets.RequestButton;
import com.preferanser.client.application.widgets.TurnPointer;
import com.preferanser.client.restygwt.RestyGwtDispatcher;
import com.preferanser.laf.client.PreferanserResources;
import com.preferanser.shared.domain.Card;
import com.preferanser.shared.domain.Contract;
import com.preferanser.shared.domain.Hand;
import com.preferanser.shared.domain.TableLocation;

import java.util.logging.Logger;

import static com.preferanser.client.restygwt.RequestIdValue.SAVE_DEAL;

public class EditorView extends BaseTableView<EditorUiHandlers> implements EditorPresenter.EditorView, CardWidget.Handlers {

    private static final Logger log = Logger.getLogger("EditorView");

    public interface Binder extends UiBinder<Widget, EditorView> {}

    @UiField Button playButton;
    @UiField Button quitButton;
    @UiField Button resetButton;
    @UiField(provided = true) RequestButton saveButton;

    @UiField Anchor eastContractAnchor;
    @UiField Anchor southContractAnchor;
    @UiField Anchor westContractAnchor;
    @UiField TextBox dealName;
    @UiField TextArea dealDescription;

    @Inject
    public EditorView(Binder uiBinder, PreferanserResources resources, PreferanserConstants constants, I18nHelper i18nHelper, RestyGwtDispatcher restyGwtDispatcher) {
        super(constants, resources, i18nHelper);
        saveButton = new RequestButton(restyGwtDispatcher, SAVE_DEAL, constants.saving(), constants.saved());
        initWidget(uiBinder.createAndBindUi(this));
        init();
    }

    @Override protected void populateHandTurnPointers() {
        super.populateHandTurnPointers();
        for (final TurnPointer turnPointer : handTurnPointerMap.values()) {
            turnPointer.addClickHandler(new ClickHandler() {
                @Override public void onClick(ClickEvent event) {
                    getUiHandlers().chooseTurn(turnPointer.getTurn());
                }
            });
        }
    }

    @Override public void displayDealName(final String name) {
        dealName.setFocus(true);
        dealName.setText(name);
        dealName.selectAll();
    }

    @Override protected void displayHandContract(Hand hand, Contract contract) {
        getHandContractTextHolder(hand).setText(i18nHelper.getContractName(contract));
    }

    @Override protected void displayNoContract(Hand hand) {
        getHandContractTextHolder(hand).setText(constants.chooseContract());
    }

    @Override protected void changeCardLocation(Card card, Optional<TableLocation> targetTableLocation) {
        getUiHandlers().changeCardLocation(card, targetTableLocation);
    }

    private Anchor getHandContractTextHolder(Hand hand) {
        switch (hand) {
            case EAST:
                return eastContractAnchor;
            case SOUTH:
                return southContractAnchor;
            case WEST:
                return westContractAnchor;
            default:
                throw new IllegalStateException("No contract link for the hand: " + hand);
        }
    }

    @UiHandler("playButton") void onPlayButtonClicked(@SuppressWarnings("unused") ClickEvent event) {
        getUiHandlers().switchToPlayer();
    }

    @UiHandler("saveButton") void onSaveButtonClicked(@SuppressWarnings("unused") ClickEvent event) {
        getUiHandlers().save(dealName.getText(), dealDescription.getText());
    }

    @UiHandler("resetButton") void onDealButtonClicked(@SuppressWarnings("unused") ClickEvent event) {
        getUiHandlers().reset();
    }

    @UiHandler("quitButton") void onQuitButtonClicked(@SuppressWarnings("unused") ClickEvent event) {
        getUiHandlers().quit();
    }

    @UiHandler("eastContractAnchor") void onEastContractLinkClicked(@SuppressWarnings("unused") ClickEvent event) {
        getUiHandlers().chooseContract(Hand.EAST);
    }

    @UiHandler("southContractAnchor") void onSouthContractLinkClicked(@SuppressWarnings("unused") ClickEvent event) {
        getUiHandlers().chooseContract(Hand.SOUTH);
    }

    @UiHandler("westContractAnchor") void onWestContractLinkClicked(@SuppressWarnings("unused") ClickEvent event) {
        getUiHandlers().chooseContract(Hand.WEST);
    }

    @Override protected Logger getLog() {
        return log;
    }
}
