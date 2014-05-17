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
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.ResetPresentersEvent;
import com.preferanser.client.application.ApplicationPresenter;
import com.preferanser.client.application.mvp.DealEvent;
import com.preferanser.client.application.mvp.TableView;
import com.preferanser.client.gwtp.NameTokens;
import com.preferanser.client.gwtp.PlaceRequestHelper;
import com.preferanser.client.service.DealService;
import com.preferanser.client.service.DrawingService;
import com.preferanser.client.service.LogResponse;
import com.preferanser.client.service.Response;
import com.preferanser.shared.domain.*;
import com.preferanser.shared.domain.exception.GameException;
import com.preferanser.shared.util.Clock;
import org.fusesource.restygwt.client.Method;

import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import static com.google.common.base.Preconditions.checkState;

/**
 * Presenter for the mvp page
 */
public class PlayerPresenter extends Presenter<PlayerPresenter.PlayerView, PlayerPresenter.Proxy>
    implements PlayerUiHandlers, DealEvent.DealCreatedHandler {

    private static final Logger log = Logger.getLogger("PlayerPresenter");

    public interface PlayerView extends TableView, HasUiHandlers<PlayerUiHandlers> {
        void displayHandTricks(Map<Hand, Integer> handTricks);
        void disableCards(Set<Card> cards);
        void displayTurnNavigation(boolean showPrev, boolean showNext);
        void displayResetButton(boolean visible);
        void displaySluffButton(boolean visible);
        void displaySaveDrawingButton(boolean visible);
    }

    private final User currentUser;
    private final PlaceManager placeManager;
    private final DealService dealService;
    private final DrawingService drawingService;
    private Optional<Long> userIdOptional = Optional.absent();
    private Optional<Long> dealIdOptional = Optional.absent();
    private Optional<Player> playerOptional = Optional.absent();

    @ProxyStandard
    @NameToken(NameTokens.PLAYER)
    public interface Proxy extends ProxyPlace<PlayerPresenter> {}

    @Inject
    public PlayerPresenter(
        PlaceManager placeManager,
        EventBus eventBus,
        PlayerView view,
        Proxy proxy,
        DealService dealService,
        DrawingService drawingService,
        User currentUser
    ) {
        super(eventBus, view, proxy, ApplicationPresenter.MAIN_SLOT);
        this.placeManager = placeManager;
        this.dealService = dealService;
        this.drawingService = drawingService;
        this.currentUser = currentUser;
        getView().setUiHandlers(this);
    }

    @Override protected void onBind() {
        super.onBind();
        addRegisteredHandler(DealEvent.getType(), this);
    }

    @Override public void prepareFromRequest(PlaceRequest request) {
        super.prepareFromRequest(request);
        PlaceRequestHelper helper = new PlaceRequestHelper(request);
        userIdOptional = helper.parseLongParameter("user");
        dealIdOptional = helper.parseLongParameter("deal");
    }

    @Override protected void onReveal() {
        super.onReveal();
        playerOptional = Optional.absent();
        if (userIdOptional.isPresent()) {
            dealService.getUserDeal(userIdOptional.get(), dealIdOptional.get(), new Response<Deal>() {
                @Override public void onSuccess(Method method, Deal deal) {
                    playerOptional = Optional.of(new Player(deal));
                    ResetPresentersEvent.fire(PlayerPresenter.this);
                }
            });
        } else {
            dealService.getCurrentUserDeal(dealIdOptional.get(), new Response<Deal>() {
                @Override public void onSuccess(Method method, Deal deal) {
                    playerOptional = Optional.of(new Player(deal));
                    ResetPresentersEvent.fire(PlayerPresenter.this);
                }
            });
        }
    }

    @Override protected void onReset() {
        super.onReset();
        prepositionCards();
        refreshView();
    }

    @Override public void onDealEvent(DealEvent dealEvent) {
        Deal deal = dealEvent.getDeal();
        playerOptional = Optional.of(new Player(deal));
    }

    @Override public void sluff() {
        if (playerOptional.get().sluffTrick())
            refreshView();
    }

    @Override public void turnFromWidow() {
        try {
            playerOptional.get().makeTurnFromWidow();
            refreshView();
        } catch (GameException e) {
            log.finer(e.getMessage());
        }
    }

    @Override public void saveDrawing() {
        checkState(playerOptional.isPresent(), "Player is not present");
        Player player = playerOptional.get();
        checkState(player.hasRedoTurns() || player.hasUndoTurns());

        Drawing drawing = new Drawing(null, userIdOptional.get(), dealIdOptional.get(), "Drawing", "Description", player.getTurns(), Clock.getNow());
        drawingService.save(drawing, new LogResponse<Long>(log, "Drawing saved"));
    }

    @Override public void changeCardLocation(Card card, Optional<TableLocation> newLocation) {
        if (newLocation.isPresent() && newLocation.get() == TableLocation.CENTER) {
            try {
                Player player = playerOptional.get();
                player.makeTurn(card);
            } catch (GameException e) {
                log.finer(e.getMessage());
            }
        }
        refreshView();
    }

    @Override public void switchToEditor() {
        PlaceRequest.Builder builder = new PlaceRequest.Builder();
        if (userIdOptional.isPresent() || dealIdOptional.isPresent()) {
            builder.nameToken(NameTokens.EDITOR);
            if (dealIdOptional.isPresent())
                builder.with("deal", dealIdOptional.get().toString());
            if (userIdOptional.isPresent())
                builder.with("user", userIdOptional.get().toString());
        } else {
            builder.nameToken(NameTokens.DEALS);
        }
        placeManager.revealPlace(builder.build());
    }

    @Override public void undo() {
        playerOptional.get().undoTurn();
        refreshView();
    }

    @Override public void redo() {
        playerOptional.get().redoTurn();
        refreshView();
    }

    @Override public void reset() {
        playerOptional.get().reset();
        refreshView();
    }

    @Override public void close() {
        revealPlace(NameTokens.DEALS);
    }

    private void revealPlace(String place) {
        placeManager.revealPlace(new PlaceRequest.Builder().nameToken(place).build());
    }

    private void prepositionCards() {
        getView().prepositionCards(Card.values());
    }

    private void refreshView() {
        if (playerOptional.isPresent()) {
            Player player = playerOptional.get();
            log.finest("Refreshing view...");
            PlayerView view = getView();
            view.displayDealInfo(player.getName(), player.getDescription());
            view.displayTurn(player.getTurn());
            view.displayCards(player.getHandCards(), player.getCenterCards(), player.getWidow());
            view.displaySluffButton(player.isTrickClosed());
            view.displayResetButton(player.hasUndoTurns() || player.hasRedoTurns());
            view.displaySaveDrawingButton(currentUser.getLoggedIn() && player.hasTurns());
            view.displayContracts(player.getHandContracts());
            view.displayHandTricks(player.getHandTrickCounts());
            view.displayTurnNavigation(player.hasUndoTurns(), player.hasRedoTurns());
            view.disableCards(player.getDisabledCards());
        } else {
            log.fine("PlayerPresenter.refreshView() skipped as playerOptional is not present");
        }
    }

}
