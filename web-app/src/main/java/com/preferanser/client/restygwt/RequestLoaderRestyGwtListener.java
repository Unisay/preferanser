package com.preferanser.client.restygwt;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.Response;
import com.google.inject.Inject;
import com.preferanser.client.application.widgets.RequestLoader;
import org.fusesource.restygwt.client.Method;

public class RequestLoaderRestyGwtListener extends RestyGwtRequestListenerAdapter {

    private final RequestLoader requestLoader;

    @Inject
    public RequestLoaderRestyGwtListener(RequestLoader requestLoader) {
        this.requestLoader = requestLoader;
    }

    @Override public void beforeRequestSent(Method method, RequestBuilder builder) {
        requestLoader.startLoader();
    }

    @Override public void afterResponseHandled(Method method, Request request, Response response) {
        requestLoader.stopLoader();
    }

    @Override public void afterClientErrorHandled(Method method, Request request, Response response) {
        requestLoader.stopLoader();
    }

    @Override public void afterServerErrorHandled(Method method, Request request, Throwable exception) {
        requestLoader.stopLoader();
    }

}