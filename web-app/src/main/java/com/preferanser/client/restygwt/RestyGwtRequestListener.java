package com.preferanser.client.restygwt;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.Response;
import org.fusesource.restygwt.client.Method;

public interface RestyGwtRequestListener {

    void beforeRequestSent(Method method, RequestBuilder builder);

    void afterRequestSent(Method method, RequestBuilder builder);

    void beforeResponseHandled(Request request, Response response);

    void afterResponseHandled(Request request, Response response);

    void beforeErrorHandled(Request request, Throwable exception);

    void afterErrorHandled(Request request, Throwable exception);
}
