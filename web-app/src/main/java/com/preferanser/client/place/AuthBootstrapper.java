package com.preferanser.client.place;

import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Bootstrapper;

public class AuthBootstrapper implements Bootstrapper {

    private final PlaceManager placeManager;

    @Inject
    public AuthBootstrapper(PlaceManager placeManager) {
        this.placeManager = placeManager;
    }

    @Override public void onBootstrap() {

    }

}
