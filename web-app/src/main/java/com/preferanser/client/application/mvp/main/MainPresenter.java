package com.preferanser.client.application.mvp.main;

import com.google.gwt.event.shared.GwtEvent;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.ContentSlot;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.client.annotations.UseGatekeeper;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealContentHandler;
import com.preferanser.client.application.ApplicationPresenter;
import com.preferanser.client.gwtp.LoggedInGatekeeper;
import com.preferanser.client.gwtp.NameTokens;

public class MainPresenter extends Presenter<MainPresenter.MainView, MainPresenter.Proxy> implements MainUiHandlers {

    public interface MainView extends View, HasUiHandlers<MainUiHandlers> {}

    @ProxyStandard
    @NameToken(NameTokens.DEALS)
    @UseGatekeeper(LoggedInGatekeeper.class)
    public interface Proxy extends ProxyPlace<MainPresenter> {}

    @ContentSlot
    public static final GwtEvent.Type<RevealContentHandler<?>> MAIN_SLOT = new GwtEvent.Type<RevealContentHandler<?>>();

    private final PlaceManager placeManager;

    @Inject
    public MainPresenter(EventBus eventBus, MainView view, Proxy proxy, PlaceManager placeManager) {
        super(eventBus, view, proxy, ApplicationPresenter.MAIN_SLOT);
        this.placeManager = placeManager;
        getView().setUiHandlers(this);
    }

    @Override public void openDealEditor() {
        placeManager.revealPlace(new PlaceRequest.Builder().nameToken(NameTokens.EDITOR).build());
    }

}
