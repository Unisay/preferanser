package com.preferanser.client.gin;

import com.google.gwt.core.client.GWT;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.annotations.DefaultPlace;
import com.gwtplatform.mvp.client.annotations.ErrorPlace;
import com.gwtplatform.mvp.client.annotations.UnauthorizedPlace;
import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;
import com.gwtplatform.mvp.client.gin.DefaultModule;
import com.gwtplatform.mvp.client.proxy.DefaultPlaceManager;
import com.preferanser.client.application.ApplicationModule;
import com.preferanser.client.place.NameTokens;
import com.preferanser.client.request.MyRequestFactory;

public class ClientModule extends AbstractPresenterModule {
    @Override
    protected void configure() {
        install(new DefaultModule(DefaultPlaceManager.class));
        install(new ApplicationModule());

        // DefaultPlaceManager Places
        bindConstant().annotatedWith(DefaultPlace.class).to(NameTokens.home);
        bindConstant().annotatedWith(ErrorPlace.class).to(NameTokens.home);
        bindConstant().annotatedWith(UnauthorizedPlace.class).to(NameTokens.home);

        bind(MyRequestFactory.class).toProvider(RequestFactoryProvider.class).in(Singleton.class);
    }

    static class RequestFactoryProvider implements Provider<MyRequestFactory> {
        private final MyRequestFactory requestFactory;

        @Inject
        public RequestFactoryProvider(EventBus eventBus) {
            requestFactory = GWT.create(MyRequestFactory.class);
            requestFactory.initialize(eventBus);
        }

        public MyRequestFactory get() {
            return requestFactory;
        }
    }
}
