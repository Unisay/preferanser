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
import com.google.gwt.i18n.client.DateTimeFormat;
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
import com.preferanser.client.application.mvp.DealEvent;
import com.preferanser.client.application.mvp.TableView;
import com.preferanser.client.application.mvp.editor.dialog.EditorDialogs;
import com.preferanser.client.gwtp.LoggedInGatekeeper;
import com.preferanser.client.gwtp.NameTokens;
import com.preferanser.client.service.DealService;
import com.preferanser.client.service.Response;
import com.preferanser.shared.domain.*;
import com.preferanser.shared.domain.exception.EditorException;
import com.preferanser.shared.domain.exception.GameException;
import org.fusesource.restygwt.client.Method;

import java.util.Date;
import java.util.logging.Logger;


/**
 * Table presenter
 */
public class EditorPresenter extends Presenter<EditorPresenter.EditorView, EditorPresenter.Proxy> implements EditorUiHandlers, HasHandContracts {

    private static final Logger log = Logger.getLogger("EditorPresenter");
    private Optional<Long> dealId = Optional.absent();

    public interface EditorView extends HasUiHandlers<EditorUiHandlers>, TableView {}

    private Editor editor;
    private final PlaceManager placeManager;
    private final DealService dealService;
    private final EditorDialogs editorDialogs;

    @ProxyStandard
    @NameToken(NameTokens.EDITOR)
    @UseGatekeeper(LoggedInGatekeeper.class)
    public interface Proxy extends ProxyPlace<EditorPresenter> {}

    @Inject
    public EditorPresenter(PlaceManager placeManager,
                           EventBus eventBus,
                           EditorView view,
                           Proxy proxy,
                           Editor editor,
                           DealService dealService,
                           EditorDialogs editorDialogs) {
        super(eventBus, view, proxy, ApplicationPresenter.MAIN_SLOT);
        this.placeManager = placeManager;
        this.editor = editor;
        this.dealService = dealService;
        this.editorDialogs = editorDialogs;
        getView().setUiHandlers(this);
        initEditor();
    }

    @Override
    public void reset() {
        initEditor();
        refreshView();
    }

    @Override public void cancel() {
        revealPlace(NameTokens.DEALS);
    }

    private void initEditor() {
        editor.reset();
        editor.setName(DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_TIME_MEDIUM).format(new Date()));
        editor.setThreePlayers();
        editor.setFirstTurn(Hand.SOUTH);
        editor.putCards(Hand.SOUTH, Card.values());
    }

    @Override
    public void prepareFromRequest(PlaceRequest request) {
        super.prepareFromRequest(request);
        String dealIdString = request.getParameter("deal", "");
        if (!dealIdString.isEmpty()) {
            try {
                this.dealId = Optional.of(Long.parseLong(dealIdString));
            } catch (NumberFormatException e) {
                placeManager.revealPlace(new PlaceRequest.Builder().nameToken(NameTokens.DEALS).build());
            }
        } else {
            dealId = Optional.absent();
        }
    }

    @Override
    protected void onReveal() {
        super.onReveal();
        if (dealId.isPresent()) {
            dealService.getById(dealId.get(), new Response<Deal>() {
                @Override public void onSuccess(Method method, Deal deal) {
                    editor.setDeal(deal);
                    refreshView();
                }
            });
        } else {
            initEditor();
            refreshView();
        }
    }

    @Override
    public void chooseContract(Hand hand) {
        editorDialogs.showContractDialog(hand);
    }

    @Override
    public void chooseTurn(Hand hand) {
        editor.setFirstTurn(hand);
        getView().displayTurn(editor.getFirstTurn());
    }

    @Override
    public void changeCardLocation(Card card, Optional<TableLocation> maybeNewLocation) {
        if (maybeNewLocation.isPresent()) {
            try {
                editor.moveCard(card, maybeNewLocation.get());
            } catch (GameException e) {
                log.finer(e.getMessage());
            }
        }
        refreshCards();
    }

    @Override
    public boolean setHandContract(Hand hand, Contract contract) {
        editor.setHandContract(hand, contract);
        refreshContracts();
        return true;
    }

    @Override
    public void save(String name, String description) {
        try {
            final Deal deal = editor.setName(name).setDescription(description).build();
            if (dealId.isPresent()) {
                // Update existing deal
                dealService.update(deal.getId(), deal, new Response<Void>() { // TODO handle failures
                    @Override public void onSuccess(Method method, Void none) {
                        DealEvent.fire(EditorPresenter.this, deal);
                        revealPlace(NameTokens.DEALS);
                    }
                });
            } else {
                // Create new deal
                dealService.persist(deal, new Response<Long>() { // TODO handle failures
                    @Override public void onSuccess(Method method, Long dealId) {
                        deal.setId(dealId);
                        DealEvent.fire(EditorPresenter.this, deal);
                        revealPlace(NameTokens.DEALS);
                    }
                });
            }
        } catch (EditorException e) {
            editorDialogs.showValidationDialog(e.getBuilderErrors());
        }
    }

    private void revealPlace(String place) {
        placeManager.revealPlace(new PlaceRequest.Builder().nameToken(place).build());
    }

    private void refreshView() {
        getView().displayDealInfo(editor.getName(), editor.getDescription());
        getView().displayTurn(editor.getFirstTurn());
        refreshContracts();
        refreshCards();
    }

    private void refreshContracts() {
        getView().displayContracts(editor.getHandContracts());
    }

    private void refreshCards() {
        getView().displayCards(editor.getHandCards(), editor.getCenterCards(), editor.getWidow());
    }

}