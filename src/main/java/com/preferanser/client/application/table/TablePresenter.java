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
import com.preferanser.shared.*;

import java.util.Collection;
import java.util.Map;

/**
 * Table presenter
 */
public class TablePresenter extends Presenter<TablePresenter.TableView, TablePresenter.Proxy> implements TableUiHandlers, HasCardinalContracts {

    private ContractDialogPresenter contractDialog;

    public interface TableView extends View, HasUiHandlers<TableUiHandlers> {

        void displayTableCards(Map<TableLocation, Collection<Card>> tableCards);

        void displayCardinalTricks(Map<Cardinal, Integer> cardinalTricks);

        void displayContracts(Map<Cardinal, Contract> cardinalContracts);

        void setPlayMode();

        void setEditMode();
    }

    private Game game = new Game();

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

    @Override protected void onReveal() {
        super.onReveal();
        if (game.getMode() == Game.Mode.PLAY) {
            getView().setPlayMode();
        } else if (game.getMode() == Game.Mode.EDIT) {
            getView().setEditMode();
        }
        reset();
    }

    @Override
    public void reset() {
        game.clearCardinalTricks();
        game.clearCardinalContracts();
        game.clearCards();
        game.putCards(Cardinal.NORTH, Card.values());
        refreshView();
    }

    @Override
    public void sluff() {
        if (game.moveCenterCardsToSluff()) {
            refreshView();
        }
    }

    @Override
    public void chooseContract(Cardinal cardinal) {
        contractDialog.setCardinal(cardinal);
        RevealRootPopupContentEvent.fire(this, contractDialog);
    }

    @Override
    public void changeCardLocation(Card card, TableLocation oldLocation, TableLocation newLocation) {
        if (oldLocation != newLocation)
            game.moveCard(card, oldLocation, newLocation);
        refreshView();
    }

    @Override
    public boolean setCardinalContract(Cardinal cardinal, Contract contract) {
        game.setCardinalContract(cardinal, contract);
        getView().displayContracts(game.getCardinalContracts());
        return true;
    }

    @Override public boolean setPlayMode() {
        game.setMode(Game.Mode.PLAY);
        getView().setPlayMode();
        return true;
    }

    @Override public boolean setEditMode() {
        game.setMode(Game.Mode.EDIT);
        getView().setEditMode();
        return true;
    }

    private void refreshView() {
        getView().displayContracts(game.getCardinalContracts());
        getView().displayTableCards(game.getCardinalCards());
        getView().displayCardinalTricks(game.getCardinalTricks());
    }
}
