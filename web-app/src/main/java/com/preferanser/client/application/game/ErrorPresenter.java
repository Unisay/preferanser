package com.preferanser.client.application.game;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.NoGatekeeper;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.preferanser.client.application.ApplicationPresenter;
import com.preferanser.client.place.NameTokens;

public class ErrorPresenter extends Presenter<ErrorPresenter.ErrorView, ErrorPresenter.Proxy> {

    @NoGatekeeper
    @ProxyStandard
    @NameToken(NameTokens.ERROR)
    public interface Proxy extends ProxyPlace<ErrorPresenter> {}

    public interface ErrorView extends View {}

    @Inject
    public ErrorPresenter(EventBus eventBus, ErrorView view, Proxy proxy) {
        super(eventBus, view, proxy, ApplicationPresenter.TYPE_SetMainContent);
    }

}
