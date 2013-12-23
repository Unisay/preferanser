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

package com.preferanser.client.application.game.editor;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.preferanser.client.application.game.BaseTableView;
import com.preferanser.client.application.i18n.I18nHelper;
import com.preferanser.client.application.i18n.PreferanserConstants;
import com.preferanser.client.application.widgets.CardWidget;
import com.preferanser.client.application.widgets.TurnPointer;
import com.preferanser.client.theme.greencloth.client.com.preferanser.client.application.PreferanserResources;
import com.preferanser.domain.Cardinal;

import java.util.Map;
import java.util.logging.Logger;

public class EditorView extends BaseTableView<EditorUiHandlers> implements EditorPresenter.EditorView, CardWidget.Handlers {

    private static final Logger log = Logger.getLogger("EditorView");

    public interface Binder extends UiBinder<Widget, EditorView> {}

    @UiField Button playButton;
    @UiField Button saveButton;
    @UiField Button dealButton;

    @UiField Hyperlink northContractLink;
    @UiField Hyperlink eastContractLink;
    @UiField Hyperlink southContractLink;
    @UiField Hyperlink westContractLink;

    @Inject
    public EditorView(Binder uiBinder, PreferanserResources resources, PreferanserConstants constants, I18nHelper i18nHelper) {
        super(constants, resources, i18nHelper);
        initWidget(uiBinder.createAndBindUi(this));
        init();
    }

    @Override protected void populateCardinalTurnPointers() {
        super.populateCardinalTurnPointers();
        for (final TurnPointer turnPointer : cardinalTurnPointerMap.values()) {
            turnPointer.addClickHandler(new ClickHandler() {
                @Override public void onClick(ClickEvent event) {
                    getUiHandlers().chooseTurn(turnPointer.getTurn());
                }
            });
        }
    }

    @Override protected Hyperlink getCardinalContractTextHolder(Cardinal cardinal) {
        switch (cardinal) {
            case NORTH:
                return northContractLink;
            case EAST:
                return eastContractLink;
            case SOUTH:
                return southContractLink;
            case WEST:
                return westContractLink;
            default:
                throw new IllegalStateException("No contract link for the cardinal: " + cardinal);
        }
    }

    @Override public void hideCardinalTricks() {
        for (Map.Entry<Cardinal, Label> entry : cardinalTricksCountMap.entrySet()) {
            entry.getValue().setText("");
        }
    }

    @UiHandler("playButton") void onPlayButtonClicked(@SuppressWarnings("unused") ClickEvent event) {
        getUiHandlers().switchToPlayer();
    }

    @UiHandler("saveButton") void onSaveButtonClicked(@SuppressWarnings("unused") ClickEvent event) {
        Window.alert("Saving of deals is not implemented yet!"); // TODO implement
    }

    @UiHandler("dealButton") void onDealButtonClicked(@SuppressWarnings("unused") ClickEvent event) {
        getUiHandlers().reset();
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

    @Override protected Logger getLog() {
        return log;
    }
}
