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
import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.ResetPresentersEvent;
import com.preferanser.client.application.ApplicationPresenter;
import com.preferanser.client.application.mvp.dialog.ApplicationDialogs;
import com.preferanser.client.application.mvp.dialog.NameDescriptionSetter;
import com.preferanser.client.application.mvp.event.DealEvent;
import com.preferanser.client.application.mvp.event.DrawingDeleteEvent;
import com.preferanser.client.application.mvp.event.DrawingOpenEvent;
import com.preferanser.client.gwtp.NameTokens;
import com.preferanser.client.gwtp.PlaceRequestHelper;
import com.preferanser.client.service.DealService;
import com.preferanser.client.service.DrawingService;
import com.preferanser.client.service.Response;
import com.preferanser.shared.domain.*;
import com.preferanser.shared.domain.exception.GameException;
import com.preferanser.shared.util.Clock;
import org.fusesource.restygwt.client.Method;

import java.util.List;
import java.util.logging.Logger;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Lists.newArrayList;

/**
 * Presenter for the mvp page
 */
public class PlayerPresenter
    extends Presenter<PlayerView, PlayerPresenter.Proxy>
    implements PlayerUiHandlers, DealEvent.DealCreatedHandler, DrawingOpenEvent.DrawingOpenHandler, DrawingDeleteEvent.DrawingDeleteHandler {

    private static final Logger log = Logger.getLogger("PlayerPresenter");

    private final User currentUser;
    private final PlaceManager placeManager;
    private final DealService dealService;
    private final DrawingService drawingService;
    private final ApplicationDialogs applicationDialogs;
    private Optional<Long> userIdOptional = Optional.absent();
    private Optional<Long> dealIdOptional = Optional.absent();
    private Optional<Long> drawingIdOptional = Optional.absent();
    private Optional<Player> playerOptional = Optional.absent();
    private List<Drawing> drawings = newArrayList();

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
        User currentUser,
        ApplicationDialogs applicationDialogs
    ) {
        super(eventBus, view, proxy, ApplicationPresenter.MAIN_SLOT);
        this.placeManager = placeManager;
        this.dealService = dealService;
        this.drawingService = drawingService;
        this.currentUser = currentUser;
        this.applicationDialogs = applicationDialogs;
        getView().setUiHandlers(this);
    }

    @Override protected void onBind() {
        super.onBind();
        addRegisteredHandler(DealEvent.getType(), this);
        addRegisteredHandler(DrawingOpenEvent.getType(), this);
        addRegisteredHandler(DrawingDeleteEvent.getType(), this);
    }

    @Override public void prepareFromRequest(PlaceRequest request) {
        super.prepareFromRequest(request);
        PlaceRequestHelper helper = new PlaceRequestHelper(request);
        userIdOptional = helper.parseLongParameter("user");
        dealIdOptional = helper.parseLongParameter("deal");
        drawingIdOptional = helper.parseLongParameter("drawing");
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

            if (drawingIdOptional.isPresent()) {
                drawingService.load(userIdOptional.get(), dealIdOptional.get(), drawingIdOptional.get(), new Response<Drawing>() {
                    @Override public void onSuccess(Method method, Drawing drawing) {
                        DrawingOpenEvent.fire(PlayerPresenter.this, drawing);
                    }
                });
            }
        } else {
            dealService.getCurrentUserDeal(dealIdOptional.get(), new Response<Deal>() {
                @Override public void onSuccess(Method method, Deal deal) {
                    playerOptional = Optional.of(new Player(deal));
                    ResetPresentersEvent.fire(PlayerPresenter.this);
                }
            });
        }

        if (currentUser.getLoggedIn()) {
            drawingService.load(dealIdOptional.get(), new Response<List<Drawing>>() {
                @Override public void onSuccess(Method method, List<Drawing> loadedDrawings) {
                    drawings.clear();
                    drawings.addAll(loadedDrawings);
                    getView().displayDrawingsButton(!loadedDrawings.isEmpty());
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

    @Override public void onDrawingOpenEvent(DrawingOpenEvent event) {
        try {
            getPlayerOrThrow().loadDrawing(event.getDrawing());
            getView().switchTabGame();
            refreshView();
        } catch (GameException e) {
            log.severe(e.getMessage());
            Window.alert(e.getMessage());
        }
    }

    @Override public void onDrawingDeleteEvent(DrawingDeleteEvent event) {
        drawingService.delete(dealIdOptional.get(), event.getDrawing().getId(), new Response<Void>());
        refreshView();
    }

    @Override public void sluff() {
        if (getPlayerOrThrow().sluffTrickIfClosed())
            refreshView();
    }

    @Override public void turnFromWidow() {
        try {
            getPlayerOrThrow().makeTurnFromWidow();
            refreshView();
        } catch (GameException e) {
            log.finer(e.getMessage());
        }
    }

    @Override public void saveDrawing() {
        final Player player = getPlayerOrThrow();
        checkState(player.hasRedoTurns() || player.hasUndoTurns());

        applicationDialogs.showSaveDrawingDialog(new NameDescriptionSetter() {
            @Override public void setNameDescription(String name, String description) {
                Drawing unsavedDrawing = new Drawing(null, userIdOptional.get(), dealIdOptional.get(), name, description, player.getTurns(), Clock.getNow());
                drawingService.save(unsavedDrawing, new Response<Drawing>() {
                    @Override public void onSuccess(Method method, Drawing savedDrawing) {
                        drawings.add(savedDrawing);
                        getView().displayDrawingsButton(true);
                    }
                });
            }
        });
    }

    @Override public void openDrawings() {
        assert !drawings.isEmpty();
        applicationDialogs.showOpenDrawingDialog(drawings);
    }

    @Override public void changeCardLocation(Card card, Optional<TableLocation> newLocation) {
        if (newLocation.isPresent() && newLocation.get() == TableLocation.CENTER) {
            try {
                getPlayerOrThrow().makeTurn(card);
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
        getPlayerOrThrow().undoTurn();
        refreshView();
    }

    @Override public void redo() {
        Player player = getPlayerOrThrow();
        player.redoTurn();
        refreshView();
    }

    @Override public void reset() {
        getPlayerOrThrow().reset();
        refreshView();
    }

    @Override public void close() {
        revealPlace(NameTokens.DEALS);
    }

    private Player getPlayerOrThrow() {
        checkState(playerOptional.isPresent(), "Player is not present");
        return playerOptional.get();
    }

    private void revealPlace(String place) {
        placeManager.revealPlace(new PlaceRequest.Builder().nameToken(place).build());
    }

    private void prepositionCards() {
        getView().prepositionCards(Card.values());
    }

    private void refreshView() {
        if (playerOptional.isPresent()) {
            refreshView(playerOptional.get(), getView());
        }
    }

    private void refreshView(Player player, PlayerView view) {
        log.finest("Refreshing view...");
        view.displayDealInfo(player.getName(), player.getDescription());
        view.displayTurn(player.getTurn());
        view.displayCards(player.getHandCards(), player.getCenterCards(), player.getWidow());
        view.displaySluffButton(player.isTrickClosed());
        view.displayResetButton(player.hasUndoTurns() || player.hasRedoTurns());
        view.displayDrawingsButton(!drawings.isEmpty());
        view.displaySaveDrawingButton(currentUser.getLoggedIn() && player.hasTurns());
        view.displayContracts(player.getHandContracts());
        view.displayHandTricks(player.getHandTrickCounts());
        view.displayTurnNavigation(player.hasUndoTurns(), player.hasRedoTurns());
        view.disableCards(player.getDisabledCards());
    }

}
