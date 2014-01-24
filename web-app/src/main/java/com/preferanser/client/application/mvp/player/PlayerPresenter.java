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
import com.google.gwt.user.client.Window;
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
import com.preferanser.client.application.i18n.PreferanserMessages;
import com.preferanser.client.application.mvp.DealCreatedEvent;
import com.preferanser.client.application.mvp.TableView;
import com.preferanser.client.gwtp.NameTokens;
import com.preferanser.shared.domain.Card;
import com.preferanser.shared.domain.Game;
import com.preferanser.shared.domain.Hand;
import com.preferanser.shared.domain.entity.Deal;
import com.preferanser.shared.domain.exception.GameException;
import com.preferanser.shared.dto.CurrentUserDto;

import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Presenter for the mvp page
 */
public class PlayerPresenter extends Presenter<PlayerPresenter.PlayerView, PlayerPresenter.Proxy>
    implements PlayerUiHandlers, DealCreatedEvent.DealCreatedHandler {

    private static final Logger log = Logger.getLogger("PlayerPresenter");

    public interface PlayerView extends TableView, HasUiHandlers<PlayerUiHandlers> {
        void displayHandTricks(Map<Hand, Integer> handTricks);

        void disableCards(Set<Card> cards);

        void displayTurnNavigation(boolean showPrev, boolean showNext);

        void displaySluffButton(boolean visible);
    }

    private PlaceManager placeManager;
    private final CurrentUserDto currentUserDto;
    private Optional<Game> gameOptional = Optional.absent();

    @ProxyStandard
    @NameToken(NameTokens.PLAYER)
    public interface Proxy extends ProxyPlace<PlayerPresenter> {}

    @Inject
    public PlayerPresenter(PlaceManager placeManager,
                           EventBus eventBus,
                           PlayerView view,
                           Proxy proxy,
                           PreferanserMessages preferanserMessages,
                           CurrentUserDto currentUserDto) {
        super(eventBus, view, proxy, ApplicationPresenter.TYPE_SetMainContent);
        this.placeManager = placeManager;
        this.currentUserDto = currentUserDto;
        getView().setUiHandlers(this);
        getView().displayAuthInfo(preferanserMessages.loggedInAs(currentUserDto.nickname));
    }

    @Override protected void onBind() {
        super.onBind();
        addRegisteredHandler(DealCreatedEvent.getType(), this);
    }

    @Override protected void onReveal() {
        super.onReveal();
        if (!gameOptional.isPresent()) {
            switchToEditor();
        } else {
            refreshView();
        }
    }

    @Override public void onDealCreated(DealCreatedEvent dealCreatedEvent) {
        Deal deal = dealCreatedEvent.getDeal();
        gameOptional = Optional.of(new Game(deal));
    }

    @Override public void sluff() {
        Preconditions.checkState(gameOptional.isPresent(), "PlayerPresenter.sluff(game is null)");
        if (gameOptional.get().sluffTrick())
            refreshView();
    }

    @Override public void makeTurn(Card card) {
        Preconditions.checkState(gameOptional.isPresent(), "PlayerPresenter.changeCardLocation(game is null)");
        try {
            gameOptional.get().makeTurn(card);
        } catch (GameException e) {
            log.finer(e.getMessage());
        }
        refreshView();
    }

    @Override public void switchToEditor() {
        placeManager.revealPlace(new PlaceRequest.Builder().nameToken(NameTokens.EDITOR).build());
    }

    @Override public void logout() {
        Window.Location.assign(currentUserDto.logoutUrl);
    }

    @Override public void undo() {
        Preconditions.checkState(gameOptional.isPresent(), "PlayerPresenter.undo(game is null)");
        gameOptional.get().undoTurn();
        refreshView();
    }

    @Override public void redo() {
        Preconditions.checkState(gameOptional.isPresent(), "PlayerPresenter.redo(game is null)");
        gameOptional.get().redoTurn();
        refreshView();
    }

    @Override public void reset() {
        Preconditions.checkState(gameOptional.isPresent(), "PlayerPresenter.reset(game is null)");
        gameOptional.get().reset();
        refreshView();
    }

    private void refreshView() {
        Game game = gameOptional.get();
        PlayerView view = getView();
        view.displayTurn(game.getTurn());
        view.displayCards(game.getHandCards(), game.getCenterCards(), game.getWidow());
        view.displaySluffButton(game.isTrickClosed());
        view.displayContracts(game.getHandContracts());
        view.displayHandTricks(game.getHandTrickCounts());
        view.displayTurnNavigation(game.hasUndoTurns(), game.hasRedoTurns());
        view.disableCards(game.getDisabledCards());
    }

}
