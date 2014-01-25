package com.preferanser.client.restygwt;

import com.google.inject.Inject;
import com.preferanser.client.application.widgets.RequestLoader;

public class RequestLoaderRestyGwtListener extends RestyGwtRequestIdListenerAdapter {

    private final RequestLoader requestLoader;

    @Inject
    public RequestLoaderRestyGwtListener(RequestLoader requestLoader) {
        this.requestLoader = requestLoader;
    }

    @Override protected void beforeRequestIdSent(RequestIdValue requestIdValue) {
        requestLoader.startLoader();
    }

    @Override protected void beforeResponseHandled(RequestIdValue requestIdValue) {
        requestLoader.stopLoader();
    }

    @Override protected void beforeRequestIdErrorHandled(RequestIdValue requestIdValue) {
        requestLoader.stopLoader();
    }

}