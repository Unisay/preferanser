package com.preferanser.client.place;

import com.google.inject.Inject;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.gwtplatform.mvp.client.Bootstrapper;
import com.preferanser.client.request.PreferanserRequestFactory;
import com.preferanser.client.request.proxy.AuthInfoProxy;

public class AuthBootstrapper implements Bootstrapper {

    private final PlaceManager placeManager;
    private final PreferanserRequestFactory requestFactory;

    @Inject
    public AuthBootstrapper(PlaceManager placeManager, PreferanserRequestFactory requestFactory) {
        this.placeManager = placeManager;
        this.requestFactory = requestFactory;
    }

    @Override public void onBootstrap() {
        requestFactory.userService().getAuthInfo().fire(new Receiver<AuthInfoProxy>() {
            @Override public void onSuccess(AuthInfoProxy response) {
                if (response.isAuthenticated()) {
                    placeManager.revealCurrentPlace();
                } else {

                }
            }
        });
    }

}
