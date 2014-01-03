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
import com.preferanser.client.application.widgets.TurnPointer;
import com.preferanser.client.theme.greencloth.client.com.preferanser.client.application.PreferanserResources;
import com.preferanser.shared.domain.Card;
import com.preferanser.shared.domain.Cardinal;
import com.preferanser.shared.domain.Contract;
import com.preferanser.shared.domain.TableLocation;

import java.util.Collection;
import java.util.Map;
import java.util.logging.Logger;

public class PlayerView extends BaseTableView<PlayerUiHandlers> implements PlayerPresenter.PlayerView {

    private static final Logger log = Logger.getLogger("PlayerView");

    public interface Binder extends UiBinder<Widget, PlayerView> {}

    @UiField Button editButton;

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

    @Override public void displayTableCards(Map<TableLocation, Collection<Card>> tableCards, Map<Card, Cardinal> centerCards) {
        super.displayTableCards(tableCards, centerCards);
        hideEmptyPanel();
    }

    @Override
    protected void displayCardinalTurnPointer(Cardinal cardinal, TurnPointer turnPointer, Cardinal turn) {
        super.displayCardinalTurnPointer(cardinal, turnPointer, turn);
        if (turnPointer.isActive())
            turnPointer.removeStyleName(tableStyle.notDisplayed());
        else
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

    @Override protected void displayCardinalContract(Cardinal cardinal, Contract contract) {
        Label label = getCardinalContractTextHolder(cardinal);
        label.setText(constants.getString(cardinal.name()) + " – " + i18nHelper.getContractName(contract).toLowerCase());
        label.setVisible(true);
    }

    @Override protected void displayNoContract(Cardinal cardinal) {
        getCardinalContractTextHolder(cardinal).setVisible(false);
    }

    private void hideEmptyPanel() {
        for (Map.Entry<TableLocation, FlowPanel> entry : table.locationPanelMap.entrySet()) {
            FlowPanel panel = entry.getValue();
            int panelCards = countCardWidgets(panel);
            if (TableLocation.CENTER != entry.getKey() && 0 == panelCards)
                table.hideLocation(entry.getKey());
        }
    }

    private int countCardWidgets(HasWidgets hasWidgets) {
        int panelCards = 0;
        for (Widget widget : hasWidgets) {
            if (widget instanceof CardWidget)
                panelCards++;
        }
        return panelCards;
    }

    private Label getCardinalContractTextHolder(Cardinal cardinal) {
        switch (cardinal) {
            case NORTH:
                return titleNorth;
            case EAST:
                return titleEast;
            case SOUTH:
                return titleSouth;
            case WEST:
                return titleWest;
            default:
                throw new IllegalStateException("No contract label for the cardinal: " + cardinal);
        }
    }

    @Override protected Logger getLog() {
        return log;
    }
}
