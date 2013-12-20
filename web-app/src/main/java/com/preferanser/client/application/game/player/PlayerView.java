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

package com.preferanser.client.application.game.player;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.preferanser.client.application.game.BaseTableView;
import com.preferanser.client.application.i18n.PreferanserConstants;
import com.preferanser.client.application.widgets.ContractLink;
import com.preferanser.client.application.widgets.TurnPointer;
import com.preferanser.client.theme.greencloth.client.com.preferanser.client.application.PreferanserResources;
import com.preferanser.domain.Cardinal;
import com.preferanser.domain.Contract;

import java.util.Map;
import java.util.logging.Logger;

import static com.google.common.collect.Maps.newHashMapWithExpectedSize;

public class PlayerView extends BaseTableView<PlayerUiHandlers> implements PlayerPresenter.PlayerView {

    private static final Logger log = Logger.getLogger("PlayerView");

    public interface Binder extends UiBinder<Widget, PlayerView> {}

    @UiField Button editButton;

    @UiField Hyperlink sluffLink;

    @UiField ContractLink northContractLink;
    @UiField ContractLink eastContractLink;
    @UiField ContractLink southContractLink;
    @UiField ContractLink westContractLink;

    protected final Map<Cardinal, ContractLink> cardinalContractMap = newHashMapWithExpectedSize(Cardinal.values().length);

    @Inject
    public PlayerView(Binder uiBinder, PreferanserResources resources, PreferanserConstants constants) {
        super(constants, resources);
        initWidget(uiBinder.createAndBindUi(this));
        init();
    }

    @Override protected void init() {
        super.init();
        installCenterPanelClickHandler();
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
    protected void displayCardinalTurnPointer(Cardinal cardinal, TurnPointer turnPointer, Cardinal turn) {
        super.displayCardinalTurnPointer(cardinal, turnPointer, turn);
        if (turnPointer.isActive()) {
            turnPointer.removeStyleName(style.notDisplayed());
        } else {
            turnPointer.addStyleName(style.notDisplayed());
        }
    }

    @Override
    public void hideTurn() {
        for (TurnPointer turnPointer : table.cardinalTurnPointerMap.values())
            turnPointer.addStyleName(style.notDisplayed());
    }

    @UiHandler("editButton") void onEditButtonClicked(@SuppressWarnings("unused") ClickEvent event) {
        getUiHandlers().switchToEditor();
    }

    @UiHandler("sluffLink") void onSluffLinkClicked(@SuppressWarnings("unused") ClickEvent event) {
        getUiHandlers().sluff();
    }

    protected void installCenterPanelClickHandler() {
        table.addCenterPanelClickHandler(new ClickHandler() {
            @Override public void onClick(ClickEvent event) {
                getUiHandlers().sluff();
            }
        });
    }

    @Override protected Logger getLog() {
        return log;
    }
}
