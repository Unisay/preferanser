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
import com.preferanser.domain.exception.GameTurnException;

import java.util.Collection;
import java.util.Map;
import java.util.logging.Logger;

import static com.google.common.collect.Lists.newArrayList;


/**
 * Table presenter
 */
public class TablePresenter extends Presenter<TablePresenter.TableView, TablePresenter.Proxy> implements TableUiHandlers, HasCardinalContracts {

    private static final Logger log = Logger.getLogger("TablePresenter");

    private ContractDialogPresenter contractDialog;
    private ValidationDialogPresenter validationDialog;
    private boolean isPlaying = false; // TODO: create 2 presenters instead of this flag

    public interface TableView extends View, HasUiHandlers<TableUiHandlers> {
        TableView setPlayMode();
        TableView setEditMode();
        TableView hideTurn();
        TableView displayTurn(Cardinal turn);
        TableView displayContracts(Map<Cardinal, Contract> cardinalContracts);
        TableView hideCardinalTricks();
        TableView displayCardinalTricks(Map<Cardinal, Integer> cardinalTricks);
        TableView displayTableCards(Map<TableLocation, Collection<Card>> tableCards, Map<Card, Cardinal> centerCards);
    }

    private GameBuilder gameBuilder;
    private Game game;

    @ProxyStandard
    @NameToken(NameTokens.TABLE)
    public interface Proxy extends ProxyPlace<TablePresenter> {}

    @Inject
    public TablePresenter(EventBus eventBus,
                          TableView view,
                          Proxy proxy,
                          GameBuilder gameBuilder,
                          ContractDialogPresenter contractDialog,
                          ValidationDialogPresenter validationDialog) {
        super(eventBus, view, proxy, ApplicationPresenter.TYPE_SetMainContent);
        this.gameBuilder = gameBuilder;
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
        // TODO: remove deal initialization once deal loading is done
        gameBuilder = new GameBuilder()
            .setThreePlayers()
            .setFirstTurn(Cardinal.NORTH)
            .setCardinalContract(Cardinal.NORTH, Contract.SEVEN_SPADE)
            .setCardinalContract(Cardinal.EAST, Contract.WHIST)
            .setCardinalContract(Cardinal.WEST, Contract.PASS)
            .putCards(Cardinal.NORTH, newArrayList(Card.values()).subList(0, 10))
            .putCards(Cardinal.EAST, newArrayList(Card.values()).subList(10, 20))
            .putCards(Cardinal.WEST, newArrayList(Card.values()).subList(20, 30));
        refreshView();
    }

    @Override
    public void sluff() {
        Preconditions.checkState(isPlaying, "TablePresenter.sluff(isPlaying==false)");
        Preconditions.checkNotNull(game, "TablePresenter.sluff(game==null)");
        if (game.sluffTrick())
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
        if (oldLocation == newLocation) {
            refreshCards();
            return;
        }

        if (isPlaying && TableLocation.CENTER != newLocation) {
            refreshCards();
            return;
        }

        try {
            if (isPlaying)
                game.makeTurn(GameUtils.tableLocationToCardinal(oldLocation), card);
            else
                gameBuilder.moveCard(card, oldLocation, newLocation);
        } catch (GameTurnException e) {
            log.finer(e.getMessage());
            refreshCards();
            return;
        }

        refreshView();
    }

    @Override
    public boolean setCardinalContract(Cardinal cardinal, Contract contract) {
        Preconditions.checkState(!isPlaying, "TablePresenter.setCardinalContract(isPlaying==true)");
        gameBuilder.setCardinalContract(cardinal, contract);
        refreshContracts();
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
        refreshTurn();
        refreshContracts();
        refreshCards();
        refreshCardinalTricks();
    }

    private void refreshTurn() {
        if (isPlaying) {
            assert game != null : "TablePresenter.refreshTurn(isPlaying==true,game==null)";
            if (game.isTrickComplete())
                getView().hideTurn();
            else
                getView().displayTurn(game.getTurn());
        } else {
            getView().displayTurn(gameBuilder.getFirstTurn());
        }
    }

    private void refreshContracts() {
        if (isPlaying) {
            assert game != null : "TablePresenter.refreshContracts(isPlaying==true,game==null)";
            getView().displayContracts(game.getCardinalContracts());
        } else {
            getView().displayContracts(gameBuilder.getCardinalContracts());
        }
    }

    private void refreshCards() {
        if (isPlaying) {
            assert game != null : "TablePresenter.refreshCards(isPlaying==true,game==null)";
            getView().displayTableCards(game.getCardinalCards(), game.getCenterCards());
        } else {
            getView().displayTableCards(gameBuilder.getTableCards(), gameBuilder.getCenterCards());
        }
    }

    private void refreshCardinalTricks() {
        if (isPlaying) {
            assert game != null : "TablePresenter.refreshCardinalTricks(isPlaying==true,game==null)";
            getView().displayCardinalTricks(game.getCardinalTricks());
        } else {
            getView().hideCardinalTricks();
        }
    }

}
