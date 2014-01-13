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

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.preferanser.client.application.ApplicationPresenter;
import com.preferanser.client.application.mvp.GameBuiltEvent;
import com.preferanser.client.application.mvp.TableView;
import com.preferanser.client.gwtp.NameTokens;
import com.preferanser.shared.domain.Card;
import com.preferanser.shared.domain.Game;
import com.preferanser.shared.domain.Hand;
import com.preferanser.shared.domain.TableLocation;
import com.preferanser.shared.domain.exception.GameException;

import java.util.logging.Logger;

/**
 * Presenter for the mvp page
 */
public class PlayerPresenter extends Presenter<PlayerPresenter.PlayerView, PlayerPresenter.Proxy> implements
        PlayerUiHandlers, GameBuiltEvent.GameBuiltHandler {

    private static final Logger log = Logger.getLogger("PlayerPresenter");

    public interface PlayerView extends TableView, HasUiHandlers<PlayerUiHandlers> {
    }

    private PlaceManager placeManager;
    private Optional<Game> gameOptional = Optional.absent();

    @ProxyStandard
    @NameToken(NameTokens.GAME_PLAYER)
    public interface Proxy extends ProxyPlace<PlayerPresenter> {
    }

    @Inject
    public PlayerPresenter(PlaceManager placeManager, EventBus eventBus, PlayerView view, Proxy proxy) {
        super(eventBus, view, proxy, ApplicationPresenter.TYPE_SetMainContent);
        this.placeManager = placeManager;
        getView().setUiHandlers(this);
    }

    @Override
    protected void onBind() {
        super.onBind();
        addRegisteredHandler(GameBuiltEvent.getType(), this);
    }

    @Override
    protected void onReveal() {
        super.onReveal();
        if (!gameOptional.isPresent()) {
            switchToEditor();
        } else {
            refreshView();
        }
    }

    @Override
    public void onGameBuilt(GameBuiltEvent gameBuiltEvent) {
        Game game = gameBuiltEvent.getGame();
        Preconditions.checkNotNull(game, "PlayerPresenter.onGameBuilt(gameBuiltEvent.getGame() is null)");
        gameOptional = Optional.of(game);
    }

    @Override
    public void sluff() {
        Preconditions.checkState(gameOptional.isPresent(), "PlayerPresenter.sluff(game is null)");
        if (gameOptional.get().sluffTrick())
            refreshView();
    }

    @Override
    public void changeCardLocation(Card card, TableLocation oldLocation, TableLocation newLocation) {
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
            gameOptional.get().makeTurn(Hand.valueOf(oldLocation), card);
        } catch (GameException e) {
            log.finer(e.getMessage());
            refreshCards();
            return;
        }

        refreshView();
    }

    @Override
    public void switchToEditor() {
        placeManager.revealPlace(new PlaceRequest.Builder().nameToken(NameTokens.GAME_EDITOR).build());
    }

    /**
     * TODO implement resetting editor
     */
    @Override
    public void reset() {
        throw new UnsupportedOperationException("Resetting editor is not implemented");
    }

    private void refreshView() {
        refreshTurn();
        refreshContracts();
        refreshCards();
        refreshHandTricks();
    }

    private void refreshTurn() {
        Preconditions.checkState(gameOptional.isPresent(), "PlayerPresenter.refreshTurn(game is null)");
        getView().displayTurn(gameOptional.get().getTurn());
    }

    private void refreshContracts() {
        Preconditions.checkState(gameOptional.isPresent(), "PlayerPresenter.refreshContracts(game is null)");
        getView().displayContracts(gameOptional.get().getHandContracts());
    }

    private void refreshCards() {
        Preconditions.checkState(gameOptional.isPresent(), "PlayerPresenter.refreshCards(game is null)");
        Game game = gameOptional.get();
        getView().displayCards(game.getHandCards(), game.getCenterCards(), game.getWidow());
    }

    private void refreshHandTricks() {
        Preconditions.checkState(gameOptional.isPresent(), "PlayerPresenter.refreshHandTricks(game is null)");
        getView().displayHandTricks(gameOptional.get().getHandTricks());
    }
}
