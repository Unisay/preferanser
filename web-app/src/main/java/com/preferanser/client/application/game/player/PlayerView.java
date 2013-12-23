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
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.preferanser.client.application.game.BaseTableView;
import com.preferanser.client.application.i18n.I18nHelper;
import com.preferanser.client.application.i18n.PreferanserConstants;
import com.preferanser.client.application.widgets.TurnPointer;
import com.preferanser.client.theme.greencloth.client.com.preferanser.client.application.PreferanserResources;
import com.preferanser.domain.Cardinal;

import java.util.logging.Logger;

public class PlayerView extends BaseTableView<PlayerUiHandlers> implements PlayerPresenter.PlayerView {

    private static final Logger log = Logger.getLogger("PlayerView");
    public interface Binder extends UiBinder<Widget, PlayerView> {}

    @UiField Button editButton;
    @UiField Label northContractLabel;
    @UiField Label eastContractLabel;
    @UiField Label southContractLabel;
    @UiField Label westContractLabel;

    @Inject
    public PlayerView(Binder uiBinder, PreferanserResources resources, PreferanserConstants constants, I18nHelper i18nHelper) {
        super(constants, resources, i18nHelper);
        initWidget(uiBinder.createAndBindUi(this));
        init();
    }

    @Override protected void init() {
        super.init();
        installCenterPanelClickHandler();
    }

    @Override
    protected void displayCardinalTurnPointer(Cardinal cardinal, TurnPointer turnPointer, Cardinal turn) {
        super.displayCardinalTurnPointer(cardinal, turnPointer, turn);
        if (turnPointer.isActive()) {
            turnPointer.removeStyleName(tableStyle.notDisplayed());
        } else {
            turnPointer.addStyleName(tableStyle.notDisplayed());
        }
    }

    @Override
    public void hideTurn() {
        for (TurnPointer turnPointer : cardinalTurnPointerMap.values())
            turnPointer.addStyleName(tableStyle.notDisplayed());
    }

    @UiHandler("editButton") void onEditButtonClicked(@SuppressWarnings("unused") ClickEvent event) {
        getUiHandlers().switchToEditor();
    }

    protected void installCenterPanelClickHandler() {
        table.addCenterPanelClickHandler(new ClickHandler() {
            @Override public void onClick(ClickEvent event) {
                getUiHandlers().sluff();
            }
        });
    }

    @Override
    protected Label getCardinalContractTextHolder(Cardinal cardinal) {
        switch (cardinal) {
            case NORTH:
                return northContractLabel;
            case EAST:
                return eastContractLabel;
            case SOUTH:
                return southContractLabel;
            case WEST:
                return westContractLabel;
            default:
                throw new IllegalStateException("No contract label for the cardinal: " + cardinal);
        }
    }

    @Override protected Logger getLog() {
        return log;
    }
}
