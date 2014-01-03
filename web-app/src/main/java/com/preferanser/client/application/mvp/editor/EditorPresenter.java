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

package com.preferanser.client.application.mvp.editor;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.client.annotations.UseGatekeeper;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.preferanser.client.application.ApplicationPresenter;
import com.preferanser.client.application.i18n.PreferanserConstants;
import com.preferanser.client.application.mvp.GameBuiltEvent;
import com.preferanser.client.application.mvp.TableView;
import com.preferanser.client.application.mvp.dialog.input.InputDialogPresenter;
import com.preferanser.client.application.mvp.editor.dialog.EditorDialogs;
import com.preferanser.client.gwtp.LoggedInGatekeeper;
import com.preferanser.client.gwtp.NameTokens;
import com.preferanser.client.service.DealService;
import com.preferanser.client.service.Response;
import com.preferanser.shared.domain.*;
import com.preferanser.shared.domain.entity.Deal;
import com.preferanser.shared.domain.exception.GameBuilderException;
import com.preferanser.shared.domain.exception.GameException;

import java.util.List;
import java.util.logging.Logger;


/**
 * Table presenter
 */
public class EditorPresenter extends Presenter<EditorPresenter.EditorView, EditorPresenter.Proxy>
    implements EditorUiHandlers, HasCardinalContracts, InputDialogPresenter.InputResultHandler {

    private static final Logger log = Logger.getLogger("EditorPresenter");

    public interface EditorView extends HasUiHandlers<EditorUiHandlers>, TableView {
        void hideCardinalTricks();
    }

    private Optional<Game> maybeGame;
    private GameBuilder gameBuilder;
    private final PlaceManager placeManager;
    private final DealService dealService;
    private final PreferanserConstants constants;
    private final EditorDialogs editorDialogs;

    @ProxyStandard
    @NameToken(NameTokens.GAME_EDITOR)
    @UseGatekeeper(LoggedInGatekeeper.class)
    public interface Proxy extends ProxyPlace<EditorPresenter> {}

    @Inject
    public EditorPresenter(PlaceManager placeManager,
                           EventBus eventBus,
                           EditorView view,
                           Proxy proxy,
                           GameBuilder gameBuilder,
                           DealService dealService,
                           PreferanserConstants constants,
                           EditorDialogs editorDialogs) {
        super(eventBus, view, proxy, ApplicationPresenter.TYPE_SetMainContent);
        this.placeManager = placeManager;
        this.gameBuilder = gameBuilder;
        this.dealService = dealService;
        this.constants = constants;
        this.editorDialogs = editorDialogs;
        getView().setUiHandlers(this);
    }

    @Override
    protected void onReveal() {
        super.onReveal();
        reset();
    }

    @Override
    public void reset() {
        maybeGame = Optional.absent();
        gameBuilder = new GameBuilder()
            .setThreePlayers()
            .setFirstTurn(Cardinal.NORTH)
            .putCards(Cardinal.NORTH, Card.values());
        refreshView();
    }

    @Override
    public void chooseContract(Cardinal cardinal) {
        editorDialogs.showContractDialog(cardinal);
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
            refreshView();
        } catch (GameException e) {
            log.finer(e.getMessage());
            refreshCards();
        }
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
            if (!maybeGame.isPresent())
                maybeGame = Optional.of(gameBuilder.build());
            placeManager.revealPlace(new PlaceRequest.Builder().nameToken(NameTokens.GAME_PLAYER).build());
            GameBuiltEvent.fire(this, maybeGame.get());
        } catch (GameBuilderException e) {
            editorDialogs.showValidationDialog(e.getBuilderErrors());
        }
    }

    @Override
    public void onDealSaveClicked() {
        editorDialogs.showInputDialog(constants.save(), constants.saveDescription());
    }

    @Override
    public void onDealOpenClicked() {
        dealService.load(new Response<List<Deal>>() {
            @Override
            protected void handle(List<Deal> deals) {
                editorDialogs.showOpenDialog(deals);
            }
        });
    }

    public void onDealOpenClicked(Deal deal) {
        gameBuilder.setDeal(deal);
        refreshView();
    }

    @Override
    public void handleInputResult(String name) {
        Deal deal = gameBuilder.buildDeal(name);
        dealService.persist(deal, new Response<Void>());
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