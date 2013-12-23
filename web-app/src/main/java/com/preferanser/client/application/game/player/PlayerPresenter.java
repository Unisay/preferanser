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

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.preferanser.client.application.ApplicationPresenter;
import com.preferanser.client.application.game.GameBuiltEvent;
import com.preferanser.client.place.NameTokens;
import com.preferanser.domain.*;
import com.preferanser.domain.exception.GameTurnException;

import java.util.Collection;
import java.util.Map;
import java.util.logging.Logger;


/**
 * Presenter for the game page
 */
public class PlayerPresenter extends Presenter<PlayerPresenter.PlayerView, PlayerPresenter.Proxy> implements PlayerUiHandlers, GameBuiltEvent.GameBuiltHandler {

    private static final Logger log = Logger.getLogger("PlayerPresenter");

    public interface PlayerView extends View, HasUiHandlers<PlayerUiHandlers> {
        void hideTurn();
        void displayTurn(Cardinal turn);
        void displayContracts(Map<Cardinal, Contract> cardinalContracts);
        void displayCardinalTricks(Map<Cardinal, Integer> cardinalTricks);
        void displayTableCards(Map<TableLocation, Collection<Card>> tableCards, Map<Card, Cardinal> centerCards);
    }

    private PlaceManager placeManager;
    private Optional<Game> gameOptional = Optional.absent();

    @ProxyStandard
    @NameToken(NameTokens.GAME_PLAYER)
    public interface Proxy extends ProxyPlace<PlayerPresenter> {}

    @Inject
    public PlayerPresenter(PlaceManager placeManager, EventBus eventBus, PlayerView view, Proxy proxy) {
        super(eventBus, view, proxy, ApplicationPresenter.TYPE_SetMainContent);
        this.placeManager = placeManager;
        getView().setUiHandlers(this);
    }

    @Override protected void onBind() {
        super.onBind();
        addRegisteredHandler(GameBuiltEvent.getType(), this);
    }

    @Override protected void onReveal() {
        super.onReveal();
        if (!gameOptional.isPresent())
            switchToEditor();
        else
            refreshView();
    }

    @Override public void onGameBuilt(GameBuiltEvent gameBuiltEvent) {
        Game game = gameBuiltEvent.getGame();
        Preconditions.checkNotNull(game, "PlayerPresenter.onGameBuilt(gameBuiltEvent.getGame() is null)");
        gameOptional = Optional.of(game);
    }

    @Override public void sluff() {
        Preconditions.checkState(gameOptional.isPresent(), "PlayerPresenter.sluff(game is null)");
        if (gameOptional.get().sluffTrick())
            refreshView();
    }

    @Override public void changeCardLocation(Card card, TableLocation oldLocation, TableLocation newLocation) {
        Preconditions.checkState(gameOptional.isPresent(), "PlayerPresenter.changeCardLocation(game is null)");

        if (oldLocation == newLocation) {
            refreshCards();
            return;
        }

        if (TableLocation.CENTER != newLocation) {
            refreshCards();
            return;
        }

        try {
            gameOptional.get().makeTurn(GameUtils.tableLocationToCardinal(oldLocation), card);
        } catch (GameTurnException e) {
            log.finer(e.getMessage());
            refreshCards();
            return;
        }

        refreshView();
    }

    @Override public void switchToEditor() {
        placeManager.revealPlace(new PlaceRequest.Builder().nameToken(NameTokens.GAME_EDITOR).build());
    }

    @Override public void reset() {
        throw new UnsupportedOperationException("Resetting editor is not implemented");
    }

    private void refreshView() {
        refreshTurn();
        refreshContracts();
        refreshCards();
        refreshCardinalTricks();
    }

    private void refreshTurn() {
        Preconditions.checkState(gameOptional.isPresent(), "PlayerPresenter.refreshTurn(game is null)");
        Game game = gameOptional.get();
        getView().displayTurn(game.getTurn());
    }

    private void refreshContracts() {
        Preconditions.checkState(gameOptional.isPresent(), "PlayerPresenter.refreshContracts(game is null)");
        getView().displayContracts(gameOptional.get().getCardinalContracts());
    }

    private void refreshCards() {
        Preconditions.checkState(gameOptional.isPresent(), "PlayerPresenter.refreshCards(game is null)");
        Game game = gameOptional.get();
        getView().displayTableCards(game.getCardinalCards(), game.getCenterCards());
    }

    private void refreshCardinalTricks() {
        Preconditions.checkState(gameOptional.isPresent(), "PlayerPresenter.refreshCardinalTricks(game is null)");
        getView().displayCardinalTricks(gameOptional.get().getCardinalTricks());
    }
}
