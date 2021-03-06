package com.preferanser.client.application.mvp.deal;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.shared.proxy.PlaceRequest;
import com.preferanser.client.application.mvp.event.DealEvent;
import com.preferanser.client.application.mvp.main.MainPresenter;
import com.preferanser.client.gwtp.NameTokens;
import com.preferanser.client.service.DealService;
import com.preferanser.client.service.Response;
import com.preferanser.shared.domain.DealInfo;
import com.preferanser.shared.domain.User;
import org.fusesource.restygwt.client.Method;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class DealPresenter extends Presenter<DealPresenter.DealView, DealPresenter.Proxy>
    implements DealUiHandlers, DealEvent.DealCreatedHandler {

    public interface DealView extends View, HasUiHandlers<DealUiHandlers> {
        void displayDeals(List<DealInfo> deals, boolean allowModifications);
    }

    @ProxyStandard
    @NameToken(NameTokens.DEALS)
    public interface Proxy extends ProxyPlace<DealPresenter> {}

    private final DealService dealService;
    private final PlaceManager placeManager;
    private final User user;
    private List<DealInfo> deals = Lists.newLinkedList();

    @Inject
    public DealPresenter(EventBus eventBus,
                         DealView view,
                         Proxy proxy,
                         PlaceManager placeManager,
                         DealService dealService,
                         User user
    ) {
        super(eventBus, view, proxy, MainPresenter.MAIN_SLOT);
        this.placeManager = placeManager;
        this.user = user;
        getView().setUiHandlers(this);
        this.dealService = dealService;
    }

    @Override protected void onBind() {
        super.onBind();
        addRegisteredHandler(DealEvent.getType(), this);
        loadDeals();
    }

    @Override public void onDealEvent(DealEvent event) {
        loadDeals();
    }

    @Override public void editDeal(DealInfo dealInfo) {
        placeManager.revealPlace(
            new PlaceRequest.Builder()
                .nameToken(NameTokens.EDITOR)
                .with("user", dealInfo.getOwnerId().toString())
                .with("deal", Long.toString(dealInfo.getId()))
                .build()
        );
    }

    @Override public void deleteDeal(final DealInfo deal) {
        Long dealId = deal.getId();
        checkNotNull(dealId, "Deal has null id, can't delete it!");
        dealService.delete(dealId, new Response<Void>() {
            @Override public void onSuccess(Method method, Void response) {
                deals.remove(deal);
                refreshView();
            }
        });
    }

    private void loadDeals() {
        dealService.load(new Response<List<DealInfo>>() {
            @Override protected void handle(List<DealInfo> loadedDeals) {
                deals.clear();
                deals.addAll(loadedDeals);
                refreshView();
            }
        });
    }

    private void refreshView() {
        Collections.sort(deals, new Comparator<DealInfo>() {
            @Override public int compare(DealInfo deal1, DealInfo deal2) {
                Date created1 = deal2.getCreated();
                Date created2 = deal1.getCreated();
                if (created1 == null)
                    return -1;
                if (created2 == null)
                    return 1;
                return created1.compareTo(created2);
            }
        });
        getView().displayDeals(deals, user.getLoggedIn());
    }

}
