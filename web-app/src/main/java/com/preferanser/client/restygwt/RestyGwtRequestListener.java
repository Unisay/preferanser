package com.preferanser.client.restygwt;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.Response;
import org.fusesource.restygwt.client.Method;

public interface RestyGwtRequestListener {

    void beforeRequestSent(Method method, RequestBuilder builder);

    void afterRequestSent(Method method, RequestBuilder builder);

    void beforeResponseHandled(Method method, Request request, Response response);

    void afterResponseHandled(Method method, Request request, Response response);

    void beforeClientErrorHandled(Method method, Request request, Response response);

    void afterClientErrorHandled(Method method, Request request, Response response);

    void beforeServerErrorHandled(Method method, Request request, Throwable exception);

    void afterServerErrorHandled(Method method, Request request, Throwable exception);

}