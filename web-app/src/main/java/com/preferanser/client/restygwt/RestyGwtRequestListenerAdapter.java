package com.preferanser.client.restygwt;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.Response;
import org.fusesource.restygwt.client.Method;

public class RestyGwtRequestListenerAdapter implements RestyGwtRequestListener {

    @Override public void beforeRequestSent(Method method, RequestBuilder builder) { }

    @Override public void afterRequestSent(Method method, RequestBuilder builder) { }

    @Override public void beforeResponseHandled(Method method, Request request, Response response) { }

    @Override public void afterResponseHandled(Method method, Request request, Response response) { }

    @Override public void beforeClientErrorHandled(Method method, Request request, Response response) { }

    @Override public void afterClientErrorHandled(Method method, Request request, Response response) { }

    @Override public void beforeServerErrorHandled(Method method, Request request, Throwable exception) { }

    @Override public void afterServerErrorHandled(Method method, Request request, Throwable exception) { }

}