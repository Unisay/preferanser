package com.preferanser.client.application.mvp.deal;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.client.annotations.UseGatekeeper;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.preferanser.client.application.ApplicationPresenter;
import com.preferanser.client.gwtp.LoggedInGatekeeper;
import com.preferanser.client.gwtp.NameTokens;
import com.preferanser.client.service.DealService;
import com.preferanser.client.service.Response;
import com.preferanser.shared.domain.entity.Deal;

import java.util.List;

public class DealPresenter extends Presenter<DealPresenter.DealView, DealPresenter.Proxy> implements DealUiHandlers {

    public interface DealView extends View, HasUiHandlers<DealUiHandlers> {
        void displayDeals(List<Deal> deals);
    }

    @ProxyStandard
    @NameToken(NameTokens.DEALS)
    @UseGatekeeper(LoggedInGatekeeper.class)
    public interface Proxy extends ProxyPlace<DealPresenter> {}

    private final DealService dealService;
    private final PlaceManager placeManager;

    @Inject
    public DealPresenter(EventBus eventBus, DealView view, Proxy proxy, PlaceManager placeManager, DealService dealService) {
        super(eventBus, view, proxy, ApplicationPresenter.TYPE_SetMainContent);
        this.placeManager = placeManager;
        getView().setUiHandlers(this);
        this.dealService = dealService;
    }

    @Override protected void onBind() {
        super.onBind();
        dealService.load(new Response<List<Deal>>() {
            @Override protected void handle(List<Deal> deals) {
                getView().displayDeals(deals);
            }
        });
    }

    @Override public void playDeal(Deal deal) {

    }

    @Override public void deleteDeal(Deal deal) {

    }

    @Override public void openDealEditor() {
        placeManager.revealPlace(new PlaceRequest.Builder().nameToken(NameTokens.GAME_EDITOR).build());
    }
}
