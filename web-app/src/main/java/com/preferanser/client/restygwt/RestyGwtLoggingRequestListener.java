package com.preferanser.client.restygwt;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.Response;
import org.fusesource.restygwt.client.Method;

import java.util.logging.Logger;

public class RestyGwtLoggingRequestListener extends RestyGwtRequestListenerAdapter {

    private static final Logger log = Logger.getLogger("RestyGwtLoggingRequestListener");

    @Override
    public void beforeRequestSent(Method method, RequestBuilder builder) {
        log.finest("Before request sent");
    }

    @Override
    public void afterRequestSent(Method method, RequestBuilder builder) {
        log.finest("After request sent");
    }

    @Override
    public void beforeResponseHandled(Request request, Response response) {
        log.finest("Before response handled");
    }

    @Override
    public void afterResponseHandled(Request request, Response response) {
        log.finest("After response handled");
    }

    @Override
    public void beforeErrorHandled(Request request, Throwable exception) {
        log.finest("Before error handled");
    }

    @Override
    public void afterErrorHandled(Request request, Throwable exception) {
        log.finest("After error handled");
    }
}
