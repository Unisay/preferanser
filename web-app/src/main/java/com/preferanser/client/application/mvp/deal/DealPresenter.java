package com.preferanser.client.application.mvp.deal;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
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
import com.preferanser.client.application.mvp.DealCreatedEvent;
import com.preferanser.client.gwtp.LoggedInGatekeeper;
import com.preferanser.client.gwtp.NameTokens;
import com.preferanser.client.service.DealService;
import com.preferanser.client.service.Response;
import com.preferanser.shared.domain.entity.Deal;
import org.fusesource.restygwt.client.Method;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DealPresenter extends Presenter<DealPresenter.DealView, DealPresenter.Proxy>
    implements DealUiHandlers, DealCreatedEvent.DealCreatedHandler {

    public interface DealView extends View, HasUiHandlers<DealUiHandlers> {
        void displayDeals(List<Deal> deals);
    }

    @ProxyStandard
    @NameToken(NameTokens.DEALS)
    @UseGatekeeper(LoggedInGatekeeper.class)
    public interface Proxy extends ProxyPlace<DealPresenter> {}

    private final DealService dealService;
    private final PlaceManager placeManager;
    private List<Deal> deals = Lists.newLinkedList();

    @Inject
    public DealPresenter(EventBus eventBus, DealView view, Proxy proxy, PlaceManager placeManager, DealService dealService) {
        super(eventBus, view, proxy, ApplicationPresenter.TYPE_SetMainContent);
        this.placeManager = placeManager;
        getView().setUiHandlers(this);
        this.dealService = dealService;
    }

    @Override protected void onBind() {
        super.onBind();
        addRegisteredHandler(DealCreatedEvent.getType(), this);
        dealService.load(new Response<List<Deal>>() {
            @Override protected void handle(List<Deal> loadedDeals) {
                deals.clear();
                deals.addAll(loadedDeals);
                refreshView();
            }
        });
    }

    @Override public void onDealCreated(DealCreatedEvent event) {
        Deal deal = event.getDeal();
        Preconditions.checkNotNull(deal.getId(), "Deal has null id, can't add it!");
        deals.add(deal);
        refreshView();
    }

    @Override public void playDeal(Deal deal) {

    }

    @Override public void deleteDeal(final Deal deal) {
        Long dealId = deal.getId();
        Preconditions.checkNotNull(dealId, "Deal has null id, can't delete it!");
        dealService.delete(dealId, new Response<Void>() {
            @Override public void onSuccess(Method method, Void response) {
                deals.remove(deal);
                refreshView();
            }
        });
    }

    @Override public void openDealEditor() {
        placeManager.revealPlace(new PlaceRequest.Builder().nameToken(NameTokens.EDITOR).build());
    }

    private void refreshView() {
        Collections.sort(deals, new Comparator<Deal>() {
            @Override public int compare(Deal deal1, Deal deal2) {
                return deal2.getCreated().compareTo(deal1.getCreated());
            }
        });
        getView().displayDeals(deals);
    }

}
