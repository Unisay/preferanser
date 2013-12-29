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

import com.google.common.base.Optional;
import com.google.gwt.user.client.Window;
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
import com.preferanser.client.application.game.GameBuiltEvent;
import com.preferanser.client.application.game.TableView;
import com.preferanser.client.application.game.dialog.input.InputDialogPresenter;
import com.preferanser.client.application.game.editor.dialog.contract.ContractDialogPresenter;
import com.preferanser.client.application.game.editor.dialog.validation.ValidationDialogPresenter;
import com.preferanser.client.application.i18n.PreferanserConstants;
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

import static com.google.common.collect.Lists.newArrayList;


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
    private final PreferanserConstants preferanserConstants;
    private final ContractDialogPresenter contractDialog;
    private final InputDialogPresenter inputDialog;
    private final ValidationDialogPresenter validationDialog;

    @ProxyStandard
    @NameToken(NameTokens.GAME_EDITOR)
    @UseGatekeeper(LoggedInGatekeeper.class)
    public interface Proxy extends ProxyPlace<EditorPresenter> {
    }

    @Inject
    public EditorPresenter(PlaceManager placeManager,
                           EventBus eventBus,
                           EditorView view,
                           Proxy proxy,
                           GameBuilder gameBuilder,
                           DealService dealService,
                           PreferanserConstants preferanserConstants,
                           ContractDialogPresenter contractDialog,
                           ValidationDialogPresenter validationDialog,
                           InputDialogPresenter inputDialog) {
        super(eventBus, view, proxy, ApplicationPresenter.TYPE_SetMainContent);
        this.placeManager = placeManager;
        this.gameBuilder = gameBuilder;
        this.dealService = dealService;
        this.preferanserConstants = preferanserConstants;
        this.contractDialog = contractDialog;
        this.inputDialog = inputDialog;
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
        maybeGame = Optional.absent();
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
        addToPopupSlot(contractDialog);
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
            validationDialog.setValidationErrors(e.getBuilderErrors());
            addToPopupSlot(validationDialog);
        }
    }

    @Override
    public void saveDeal() {
        inputDialog.setTitle(preferanserConstants.save());
        inputDialog.setDescription(preferanserConstants.saveDescription());
        inputDialog.onInputResult(this);
        addToPopupSlot(inputDialog, true);
    }

    @Override
    public void openDeal() {
        dealService.load(new Response<List<Deal>>(){
            @Override protected void handle(List<Deal> response) {
                Window.alert("Loaded deals: " + response.size());
            }
        });
    }

    @Override
    public void handleInputResult(String name) {
        if (name != null && !"".equals(name.trim())) {
            inputDialog.getView().hide();
            Deal deal = Deal.fromGameBuilder(gameBuilder);
            deal.setName(name);
            dealService.persist(deal, new Response<Void>());
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
