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

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealRootPopupContentEvent;
import com.preferanser.client.application.ApplicationPresenter;
import com.preferanser.client.application.game.editor.dialog.contract.ContractDialogPresenter;
import com.preferanser.client.application.game.editor.dialog.validation.ValidationDialogPresenter;
import com.preferanser.client.application.game.TableView;
import com.preferanser.client.place.NameTokens;
import com.preferanser.domain.*;
import com.preferanser.domain.exception.GameBuilderException;
import com.preferanser.domain.exception.GameTurnException;

import java.util.logging.Logger;

import static com.google.common.collect.Lists.newArrayList;


/**
 * Table presenter
 */
public class EditorPresenter extends Presenter<EditorPresenter.EditorView, EditorPresenter.Proxy>
        implements EditorUiHandlers, HasCardinalContracts {

    private static final Logger log = Logger.getLogger("EditorPresenter");

    private ContractDialogPresenter contractDialog;
    private ValidationDialogPresenter validationDialog;

    public interface EditorView extends HasUiHandlers<EditorUiHandlers>, TableView {
        void hideCardinalTricks();
    }

    private GameBuilder gameBuilder;

    @ProxyStandard
    @NameToken(NameTokens.GAME_BUILDER)
    public interface Proxy extends ProxyPlace<EditorPresenter> {}

    @Inject
    public EditorPresenter(EventBus eventBus,
                           EditorView view,
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
        reset();
    }

    @Override
    public void reset() {
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
    public void chooseContract(Cardinal cardinal) {
        contractDialog.setCardinal(cardinal);
        RevealRootPopupContentEvent.fire(this, contractDialog);
    }

    @Override
    public void chooseTurn(Cardinal cardinal) {
        gameBuilder.setFirstTurn(cardinal);
        getView().displayTurn(gameBuilder.getFirstTurn());
    }

    @Override
    public void changeCardLocation(Card card, TableLocation oldLocation, TableLocation newLocation) {
        if (oldLocation == newLocation) {
            refreshCards();
            return;
        }

        try {
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
        gameBuilder.setCardinalContract(cardinal, contract);
        refreshContracts();
        return true;
    }

    @Override
    public void switchToPlayer() {
        try {
            Game game = gameBuilder.build();
        } catch (GameBuilderException e) {
            validationDialog.setValidationErrors(e.getBuilderErrors());
            RevealRootPopupContentEvent.fire(this, validationDialog);
        }
    }

    private void refreshView() {
        refreshTurn();
        refreshContracts();
        refreshCards();
        refreshCardinalTricks();
    }

    private void refreshTurn() {
        getView().displayTurn(gameBuilder.getFirstTurn());
    }

    private void refreshContracts() {
        getView().displayContracts(gameBuilder.getCardinalContracts());
    }

    private void refreshCards() {
        getView().displayTableCards(gameBuilder.getTableCards(), gameBuilder.getCenterCards());
    }

    private void refreshCardinalTricks() {
        getView().hideCardinalTricks();
    }

}
