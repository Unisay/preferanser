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
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.NoGatekeeper;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealRootPopupContentEvent;
import com.preferanser.client.application.ApplicationPresenter;
import com.preferanser.client.application.game.GameBuiltEvent;
import com.preferanser.client.application.game.TableView;
import com.preferanser.client.application.game.editor.dialog.contract.ContractDialogPresenter;
import com.preferanser.client.application.game.editor.dialog.validation.ValidationDialogPresenter;
import com.preferanser.client.place.NameTokens;
import com.preferanser.shared.domain.*;
import com.preferanser.shared.domain.exception.GameBuilderException;
import com.preferanser.shared.domain.exception.GameException;

import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import static com.google.common.collect.Lists.newArrayList;


/**
 * Table presenter
 */
public class EditorPresenter extends Presenter<EditorPresenter.EditorView, EditorPresenter.Proxy>
    implements EditorUiHandlers, HasCardinalContracts {

    private static final Logger log = Logger.getLogger("EditorPresenter");

    public interface EditorView extends HasUiHandlers<EditorUiHandlers>, TableView {
        void hideCardinalTricks();
    }

    private Optional<Game> maybeGame;
    private GameBuilder gameBuilder;
    private final PlaceManager placeManager;
    private final ContractDialogPresenter contractDialog;
    private final ValidationDialogPresenter validationDialog;

    @NoGatekeeper
    @ProxyStandard
    @NameToken(NameTokens.GAME_EDITOR)
    public interface Proxy extends ProxyPlace<EditorPresenter> {}

    @Inject
    public EditorPresenter(PlaceManager placeManager,
                           EventBus eventBus,
                           EditorView view,
                           Proxy proxy,
                           GameBuilder gameBuilder,
                           ContractDialogPresenter contractDialog,
                           ValidationDialogPresenter validationDialog) {
        super(eventBus, view, proxy, ApplicationPresenter.TYPE_SetMainContent);
        this.placeManager = placeManager;
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
        } catch (GameException e) {
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
            if (!maybeGame.isPresent())
                maybeGame = Optional.of(gameBuilder.build());
            placeManager.revealPlace(new PlaceRequest.Builder().nameToken(NameTokens.GAME_PLAYER).build());
            GameBuiltEvent.fire(this, maybeGame.get());
        } catch (GameBuilderException e) {
            validationDialog.setValidationErrors(e.getBuilderErrors());
            RevealRootPopupContentEvent.fire(this, validationDialog);
        }
    }

    @Override public void saveDeal() {
/*        PreferanserRequestFactory.DealServiceRequest dealServiceRequest = requestFactory.dealService();

        DealProxy dealProxy = dealServiceRequest.create(DealProxy.class);
        dealProxy.setFirstTurn(gameBuilder.getFirstTurn());
        dealProxy.setCreated(new Date());
        dealProxy.setName(new Date().toString()); // TODO: ask user for a name

        Map<Cardinal, Contract> cardinalContracts = gameBuilder.getCardinalContracts();
        dealProxy.setNorthContract(cardinalContracts.get(Cardinal.NORTH));
        dealProxy.setEastContract(cardinalContracts.get(Cardinal.EAST));
        dealProxy.setSouthContract(cardinalContracts.get(Cardinal.SOUTH));
        dealProxy.setWestContract(cardinalContracts.get(Cardinal.WEST));

        Map<TableLocation, Collection<Card>> tableCards = gameBuilder.getTableCards();
        dealProxy.setNorthCards(newArrayListOrEmpty(tableCards.get(TableLocation.NORTH)));
        dealProxy.setEastCards(newArrayListOrEmpty(tableCards.get(TableLocation.EAST)));
        dealProxy.setSouthCards(newArrayListOrEmpty(tableCards.get(TableLocation.SOUTH)));
        dealProxy.setWestCards(newArrayListOrEmpty(tableCards.get(TableLocation.WEST)));

        Map<Card, Cardinal> centerCards = gameBuilder.getCenterCards();
        for (Map.Entry<Card, Cardinal> cardCardinalEntry : centerCards.entrySet()) {
            switch (cardCardinalEntry.getValue()) {
                case NORTH:
                    dealProxy.setCenterNorthCard(cardCardinalEntry.getKey());
                    break;
                case EAST:
                    dealProxy.setCenterEastCard(cardCardinalEntry.getKey());
                    break;
                case SOUTH:
                    dealProxy.setCenterSouthCard(cardCardinalEntry.getKey());
                    break;
                case WEST:
                    dealProxy.setCenterWestCard(cardCardinalEntry.getKey());
                    break;
                default:
                    throw new IllegalStateException("Invalid Cardinal constant: " + cardCardinalEntry.getValue());
            }
        }

        dealServiceRequest.persist(dealProxy).fire();*/
    }

    private List<Card> newArrayListOrEmpty(Iterable<Card> cards) {
        if (cards == null)
            return Collections.emptyList();
        return newArrayList(cards);
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
