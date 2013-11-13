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

import com.google.common.base.Preconditions;
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
import com.preferanser.client.application.table.dialog.validation.ValidationDialogPresenter;
import com.preferanser.client.place.NameTokens;
import com.preferanser.domain.*;
import com.preferanser.domain.exception.GameBuilderException;

import java.util.Collection;
import java.util.Map;

/**
 * Table presenter
 */
public class TablePresenter extends Presenter<TablePresenter.TableView, TablePresenter.Proxy> implements TableUiHandlers, HasCardinalContracts {

    private ContractDialogPresenter contractDialog;
    private ValidationDialogPresenter validationDialog;
    private boolean isPlaying = false;

    public interface TableView extends View, HasUiHandlers<TableUiHandlers> {
        TableView setPlayMode();
        TableView setEditMode();
        TableView displayTurn(Cardinal turn);
        TableView displayContracts(Map<Cardinal, Contract> cardinalContracts);
        TableView displayCardinalTricks(Map<Cardinal, Integer> cardinalTricks);
        TableView displayTableCards(Map<TableLocation, Collection<Card>> tableCards, Map<Card, Cardinal> centerCards);
        TableView hideCardinalTricks();
    }

    private GameBuilder gameBuilder = new GameBuilder().setThreePlayers();
    private Game game;

    @ProxyStandard
    @NameToken(NameTokens.TABLE)
    public interface Proxy extends ProxyPlace<TablePresenter> {}

    @Inject
    public TablePresenter(EventBus eventBus,
                          TableView view,
                          Proxy proxy,
                          ContractDialogPresenter contractDialog,
                          ValidationDialogPresenter validationDialog) {
        super(eventBus, view, proxy, ApplicationPresenter.TYPE_SetMainContent);
        this.contractDialog = contractDialog;
        this.contractDialog.setHasCardinalContracts(this);
        this.validationDialog = validationDialog;
        getView().setUiHandlers(this);
    }

    @Override
    protected void onReveal() {
        super.onReveal();
        if (isPlaying) {
            getView().setPlayMode();
        } else {
            getView().setEditMode();
        }
        reset();
    }

    @Override
    public void reset() {
        Preconditions.checkState(!isPlaying, "TablePresenter.reset(isPlaying==true)");
        gameBuilder = new GameBuilder()
                .setThreePlayers()
                .putCards(Cardinal.NORTH, Card.values());
        refreshView();
    }

    @Override
    public void sluff() {
        Preconditions.checkState(isPlaying, "TablePresenter.sluff(isPlaying==false)");
        Preconditions.checkNotNull(game, "TablePresenter.sluff(game==null)");
        if (game.moveCenterCardsToSluff())
            refreshView();
    }

    @Override
    public void chooseContract(Cardinal cardinal) {
        Preconditions.checkState(!isPlaying, "TablePresenter.chooseContract(isPlaying==true)");
        contractDialog.setCardinal(cardinal);
        RevealRootPopupContentEvent.fire(this, contractDialog);
    }

    @Override
    public void chooseTurn(Cardinal cardinal) {
        Preconditions.checkState(!isPlaying, "TablePresenter.chooseTurn(isPlaying==true)");
        gameBuilder.setFirstTurn(cardinal);
        getView().displayTurn(gameBuilder.getFirstTurn());
    }

    @Override
    public void changeCardLocation(Card card, TableLocation oldLocation, TableLocation newLocation) {
        if (oldLocation != newLocation)
            gameBuilder.moveCard(card, oldLocation, newLocation);
        refreshView();
    }

    @Override
    public boolean setCardinalContract(Cardinal cardinal, Contract contract) {
        Preconditions.checkState(!isPlaying, "TablePresenter.setCardinalContract(isPlaying==true)");
        gameBuilder.setCardinalContract(cardinal, contract);
        getView().displayContracts(gameBuilder.getCardinalContracts());
        return true;
    }

    @Override
    public boolean setPlayMode() {
        try {
            game = gameBuilder.build();
            getView().setPlayMode();
            isPlaying = true;
            return true;
        } catch (GameBuilderException e) {
            validationDialog.setValidationErrors(e.getBuilderErrors());
            RevealRootPopupContentEvent.fire(this, validationDialog);
            return false;
        }
    }

    @Override
    public boolean setEditMode() {
        isPlaying = false;
        getView().setEditMode();
        return true;
    }

    private void refreshView() {
        if (isPlaying) {
            assert game != null : "TablePresenter.refreshView(isPlaying==true,game==null)";
            getView()
                    .displayTurn(game.getTurn())
                    .displayContracts(game.getCardinalContracts())
                    .displayTableCards(game.getCardinalCards(), game.getCenterCards())
                    .displayCardinalTricks(game.getCardinalTricks());
        } else {
            getView()
                    .displayTurn(gameBuilder.getFirstTurn())
                    .displayContracts(gameBuilder.getCardinalContracts())
                    .displayTableCards(gameBuilder.getTableCards(), gameBuilder.getCenterCards())
                    .hideCardinalTricks();
        }
    }
}
