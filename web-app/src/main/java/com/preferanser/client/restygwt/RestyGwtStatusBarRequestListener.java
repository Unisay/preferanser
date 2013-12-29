package com.preferanser.client.restygwt;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.Response;
import com.google.inject.Inject;
import com.preferanser.client.application.widgets.StatusBar;
import org.fusesource.restygwt.client.Method;

public class RestyGwtStatusBarRequestListener extends RestyGwtRequestListenerAdapter {

    private StatusBar statusBar;

    @Inject
    public RestyGwtStatusBarRequestListener(StatusBar statusBar) {
        this.statusBar = statusBar;
    }

    @Override
    public void beforeRequestSent(Method method, RequestBuilder builder) {
        statusBar.showStatus(builder.getHTTPMethod() + "...");
    }

    @Override
    public void afterResponseHandled(Method method, Request request, Response response) {
        statusBar.hideStatus();
    }

    @Override
    public void afterErrorHandled(Method method, Request request, Throwable exception) {
        statusBar.hideStatus();
    }
}
