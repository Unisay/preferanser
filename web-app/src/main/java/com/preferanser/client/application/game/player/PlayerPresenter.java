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

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.preferanser.client.application.ApplicationPresenter;
import com.preferanser.client.place.NameTokens;
import com.preferanser.domain.*;
import com.preferanser.domain.exception.GameTurnException;

import java.util.Collection;
import java.util.Map;
import java.util.logging.Logger;


/**
 * Presenter for the game page
 */
public class PlayerPresenter extends Presenter<PlayerPresenter.PlayerView, PlayerPresenter.Proxy> implements PlayerUiHandlers {

    private static final Logger log = Logger.getLogger("PlayerPresenter");

    public interface PlayerView extends View, HasUiHandlers<PlayerUiHandlers> {
        void hideTurn();
        void displayTurn(Cardinal turn);
        void displayContracts(Map<Cardinal, Contract> cardinalContracts);
        void displayCardinalTricks(Map<Cardinal, Integer> cardinalTricks);
        void displayTableCards(Map<TableLocation, Collection<Card>> tableCards, Map<Card, Cardinal> centerCards);
    }

    private Game game;

    @ProxyStandard
    @NameToken(NameTokens.GAME)
    public interface Proxy extends ProxyPlace<PlayerPresenter> {}

    @Inject
    public PlayerPresenter(EventBus eventBus, PlayerView view, Proxy proxy) {
        super(eventBus, view, proxy, ApplicationPresenter.TYPE_SetMainContent);
        getView().setUiHandlers(this);
    }

    @Override
    public void sluff() {
        Preconditions.checkNotNull(game, "PlayerPresenter.sluff(game==null)");
        if (game.sluffTrick())
            refreshView();
    }

    @Override
    public void changeCardLocation(Card card, TableLocation oldLocation, TableLocation newLocation) {
        if (oldLocation == newLocation) {
            refreshCards();
            return;
        }

        if (TableLocation.CENTER != newLocation) {
            refreshCards();
            return;
        }

        try {
            game.makeTurn(GameUtils.tableLocationToCardinal(oldLocation), card);
        } catch (GameTurnException e) {
            log.finer(e.getMessage());
            refreshCards();
            return;
        }

        refreshView();
    }

    @Override public void switchToEditor() {
        throw new UnsupportedOperationException("Switching to editor is not implemented");
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
        assert game != null : "PlayerPresenter.refreshTurn(game==null)";
        if (game.isTrickComplete())
            getView().hideTurn();
        else
            getView().displayTurn(game.getTurn());
    }

    private void refreshContracts() {
        assert game != null : "PlayerPresenter.refreshContracts(game==null)";
        getView().displayContracts(game.getCardinalContracts());
    }

    private void refreshCards() {
        assert game != null : "PlayerPresenter.refreshCards(game==null)";
        getView().displayTableCards(game.getCardinalCards(), game.getCenterCards());
    }

    private void refreshCardinalTricks() {
        assert game != null : "PlayerPresenter.refreshCardinalTricks(game==null)";
        getView().displayCardinalTricks(game.getCardinalTricks());
    }

}
