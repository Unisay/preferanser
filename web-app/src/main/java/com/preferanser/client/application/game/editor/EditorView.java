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
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.preferanser.client.application.game.BaseTableView;
import com.preferanser.client.application.i18n.PreferanserConstants;
import com.preferanser.client.application.widgets.CardWidget;
import com.preferanser.client.application.widgets.ContractLink;
import com.preferanser.client.application.widgets.TurnPointer;
import com.preferanser.client.theme.greencloth.client.com.preferanser.client.application.PreferanserResources;
import com.preferanser.domain.Cardinal;
import com.preferanser.domain.Contract;

import java.util.Map;
import java.util.logging.Logger;

import static com.google.common.collect.Maps.newHashMapWithExpectedSize;

public class EditorView extends BaseTableView<EditorUiHandlers> implements EditorPresenter.EditorView, CardWidget.Handlers {

    private static final Logger log = Logger.getLogger("EditorView");

    public interface Binder extends UiBinder<Widget, EditorView> {}

    @UiField Button playButton;
    @UiField Button saveButton;
    @UiField Button dealButton;

    @UiField Hyperlink sluffLink;
    @UiField ContractLink northContractLink;
    @UiField ContractLink eastContractLink;
    @UiField ContractLink southContractLink;
    @UiField ContractLink westContractLink;

    protected final Map<Cardinal, ContractLink> cardinalContractMap = newHashMapWithExpectedSize(Cardinal.values().length);

    @Inject
    public EditorView(Binder uiBinder, PreferanserResources resources, PreferanserConstants constants) {
        super(constants, resources);
        initWidget(uiBinder.createAndBindUi(this));
        init();
    }

    @Override protected void init() {
        super.init();
        populateCardinalContractLinks();
    }

    public void displayContracts(Map<Cardinal, Contract> cardinalContracts) {
        for (Cardinal cardinal : Cardinal.values()) {
            ContractLink contractLink = cardinalContractMap.get(cardinal);
            if (cardinalContracts.containsKey(cardinal)) {
                Contract contract = cardinalContracts.get(cardinal);
                contractLink.setContract(contract);
            } else {
                contractLink.setContract(null);
            }
        }
    }

    @Override
    public void hideCardinalTricks() {
        for (Map.Entry<Cardinal, Label> entry : cardinalTricksCountMap.entrySet()) {
            entry.getValue().setText("");
        }
    }

    protected void populateCardinalContractLinks() {
        cardinalContractMap.put(Cardinal.NORTH, northContractLink);
        cardinalContractMap.put(Cardinal.EAST, eastContractLink);
        cardinalContractMap.put(Cardinal.SOUTH, southContractLink);
        cardinalContractMap.put(Cardinal.WEST, westContractLink);
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

    @UiFactory TurnPointer turnPointer() {
        final TurnPointer turnPointer = new TurnPointer(style, resources.arrowRight());
        turnPointer.addClickHandler(new ClickHandler() {
            @Override public void onClick(ClickEvent event) {
                getUiHandlers().chooseTurn(turnPointer.getTurn());
            }
        });
        return turnPointer;
    }

    @Override protected Logger getLog() {
        return log;
    }
}
