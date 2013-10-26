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

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealRootPopupContentEvent;
import com.preferanser.client.application.ApplicationPresenter;
import com.preferanser.client.application.table.dialog.contract.ContractDialogPresenter;
import com.preferanser.client.place.NameTokens;
import com.preferanser.shared.Card;
import com.preferanser.shared.Cardinal;
import com.preferanser.shared.Contract;
import com.preferanser.shared.TableLocation;

import java.util.Map;

import static com.preferanser.shared.TableLocation.CENTER;
import static com.preferanser.shared.TableLocation.NORTH;

/**
 * Table presenter
 */
public class TablePresenter extends Presenter<TablePresenter.TableView, TablePresenter.Proxy> implements TableUiHandlers, HasCardinalContracts {

    private ContractDialogPresenter contractDialog;

    public interface TableView extends View, HasUiHandlers<TableUiHandlers> {

        void displayTableCards(Multimap<TableLocation, Card> tableCards);

        void setTrickCounts(Map<Cardinal, Integer> trickCounts);
    }

    private Multimap<TableLocation, Card> tableCards = HashMultimap.create(5, 28);
    private Map<Cardinal, Integer> trickCounts = Maps.newHashMapWithExpectedSize(Cardinal.values().length);

    @ProxyStandard
    @NameToken(NameTokens.TABLE)
    public interface Proxy extends ProxyPlace<TablePresenter> {}

    @Inject
    public TablePresenter(EventBus eventBus, TableView view, Proxy proxy, ContractDialogPresenter contractDialog) {
        super(eventBus, view, proxy, ApplicationPresenter.TYPE_SetMainContent);
        this.contractDialog = contractDialog;
        this.contractDialog.setHasCardinalContracts(this);
        getView().setUiHandlers(this);
    }

    @Override
    protected void onBind() {
        super.onBind();
        for (Cardinal cardinal : Cardinal.values()) {
            trickCounts.put(cardinal, 0);
        }
    }

    @Override
    public void dealCards() {
        tableCards.clear();
        tableCards.putAll(NORTH, Card.valuesAsSet());
        refreshView();
    }

    @Override
    public void sluff() {
        if (tableCards.get(CENTER).size() > 2) {
            tableCards.get(CENTER).clear();
            refreshView();
        }
    }

    @Override
    public void chooseContract(Cardinal cardinal) {
        contractDialog.setCardinal(cardinal);
        RevealRootPopupContentEvent.fire(this, contractDialog);
    }

    @Override
    public boolean changeCardLocation(Card card, TableLocation oldLocation, TableLocation newLocation) {
        if (CENTER == newLocation && tableCards.get(CENTER).size() == 4) {
            refreshView();
            return false;
        }

        tableCards.remove(oldLocation, card);
        tableCards.put(newLocation, card);
        refreshView();
        return true;
    }

    @Override
    public boolean setCardinalContract(Cardinal cardinal, Contract contract) {
        return true;
    }

    private void refreshView() {
        getView().displayTableCards(tableCards);
        getView().setTrickCounts(trickCounts);
    }
}
