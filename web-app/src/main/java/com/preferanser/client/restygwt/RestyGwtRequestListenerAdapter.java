package com.preferanser.client.restygwt;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.Response;
import com.preferanser.client.restygwt.RestyGwtRequestListener;
import org.fusesource.restygwt.client.Method;

public class RestyGwtRequestListenerAdapter implements RestyGwtRequestListener {

    @Override
    public void beforeRequestSent(Method method, RequestBuilder builder) {
    }

    @Override
    public void afterRequestSent(Method method, RequestBuilder builder) {
    }

    @Override
    public void beforeResponseHandled(Request request, Response response) {
    }

    @Override
    public void afterResponseHandled(Request request, Response response) {
    }

    @Override
    public void beforeErrorHandled(Request request, Throwable exception) {
    }

    @Override
    public void afterErrorHandled(Request request, Throwable exception) {
    }
}
