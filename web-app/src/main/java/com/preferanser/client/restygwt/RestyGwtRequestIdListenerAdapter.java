package com.preferanser.client.restygwt;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.Response;
import org.fusesource.restygwt.client.Method;

import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;

public abstract class RestyGwtRequestIdListenerAdapter implements RestyGwtRequestListener {

    private List<RequestIdValue> requestIds;

    protected RestyGwtRequestIdListenerAdapter() {
        this(RequestIdValue.values());
    }

    public RestyGwtRequestIdListenerAdapter(RequestIdValue... requestIds) {
        this(newArrayList(requestIds));
    }

    public RestyGwtRequestIdListenerAdapter(List<RequestIdValue> requestIds) {
        setRequestIds(requestIds);
    }

    @Override
    public final void beforeRequestSent(Method method, RequestBuilder builder) {
        RequestIdValue requestIdValue = getRequestIdValue(method);
        if (requestIds.contains(requestIdValue))
            beforeRequestIdSent(requestIdValue);
    }

    @SuppressWarnings("unused")
    protected void beforeRequestIdSent(RequestIdValue requestIdValue) {
    }

    @Override
    public final void afterRequestSent(Method method, RequestBuilder builder) {
        RequestIdValue requestIdValue = getRequestIdValue(method);
        if (requestIds.contains(requestIdValue))
            afterRequestIdSent(requestIdValue);
    }

    @SuppressWarnings("unused")
    protected void afterRequestIdSent(RequestIdValue requestIdValue) {
    }

    @Override
    public final void beforeResponseHandled(Method method, Request request, Response response) {
        RequestIdValue requestIdValue = getRequestIdValue(method);
        if (requestIds.contains(requestIdValue))
            beforeResponseHandled(requestIdValue);
    }

    @SuppressWarnings("unused")
    protected void beforeResponseHandled(RequestIdValue requestIdValue) {
    }

    @Override
    public final void afterResponseHandled(Method method, Request request, Response response) {
        RequestIdValue requestIdValue = getRequestIdValue(method);
        if (requestIds.contains(requestIdValue))
            afterResponseHandled(requestIdValue);
    }

    @SuppressWarnings("unused")
    protected void afterResponseHandled(RequestIdValue requestIdValue) {
    }

    @Override
    public final void beforeErrorHandled(Method method, Request request, Throwable exception) {
        RequestIdValue requestIdValue = getRequestIdValue(method);
        if (requestIds.contains(requestIdValue))
            beforeRequestIdErrorHandled(requestIdValue);
    }

    @SuppressWarnings("unused")
    protected void beforeRequestIdErrorHandled(RequestIdValue requestIdValue) {
    }

    @Override
    public final void afterErrorHandled(Method method, Request request, Throwable exception) {
        RequestIdValue requestIdValue = getRequestIdValue(method);
        if (requestIds.contains(requestIdValue))
            afterRequestIdErrorHandled(requestIdValue);
    }

    @SuppressWarnings("unused")
    protected void afterRequestIdErrorHandled(RequestIdValue requestIdValue) {
    }

    public RequestIdValue getRequestIdValue(Method method) {
        Map<String, String> methodData = method.getData();
        if (methodData.containsKey("requestId")) {
            String value = methodData.get("requestId");
            String unquotedValue = value.substring(2, value.length() - 2);
            return RequestIdValue.valueOf(unquotedValue);
        }
        return null;
    }

    public void setRequestIds(List<RequestIdValue> requestIds) {
        this.requestIds = requestIds;
    }
}
