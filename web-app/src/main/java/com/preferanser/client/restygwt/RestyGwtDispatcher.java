package com.preferanser.client.restygwt;

import com.google.gwt.http.client.*;
import com.google.inject.Inject;
import org.fusesource.restygwt.client.Defaults;
import org.fusesource.restygwt.client.Dispatcher;
import org.fusesource.restygwt.client.Method;

import java.util.List;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;

public class RestyGwtDispatcher implements Dispatcher {

    @SuppressWarnings("unused") // required by RestyGWT
    public static final RestyGwtDispatcher INSTANCE = new RestyGwtDispatcher();

    private List<RestyGwtRequestListener> requestListeners;

    /**
     * Provides the {@link RestyGwtDispatcher} and set it as default Resty {@link Dispatcher}.
     */
    public static class Provider implements com.google.inject.Provider<RestyGwtDispatcher> {

        private Set<RestyGwtRequestListener> requestListeners;

        @Inject
        public Provider(Set<RestyGwtRequestListener> requestListeners) {
            this.requestListeners = requestListeners;
        }

        @Override
        public RestyGwtDispatcher get() {
            RestyGwtDispatcher restyGwtDispatcher = new RestyGwtDispatcher();
            for (RestyGwtRequestListener requestListener : requestListeners)
                restyGwtDispatcher.addRequestListener(requestListener);
            Defaults.setDispatcher(restyGwtDispatcher);
            Defaults.setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZZ");
            return restyGwtDispatcher;
        }
    }

    @Override
    public Request send(final Method method, RequestBuilder builder) throws RequestException {
        final RequestCallback callback = builder.getCallback();
        builder.setCallback(new RequestCallback() {
            @Override
            public void onResponseReceived(Request request, Response response) {
                if (response.getStatusCode() == Response.SC_OK) {
                    for (RestyGwtRequestListener requestListener : requestListeners)
                        requestListener.beforeResponseHandled(method, request, response);
                    callback.onResponseReceived(request, response);
                    for (RestyGwtRequestListener requestListener : requestListeners)
                        requestListener.afterResponseHandled(method, request, response);
                } else {
                    for (RestyGwtRequestListener requestListener : requestListeners)
                        requestListener.beforeClientErrorHandled(method, request, response);
                    callback.onResponseReceived(request, response);
                    for (RestyGwtRequestListener requestListener : requestListeners)
                        requestListener.afterClientErrorHandled(method, request, response);
                }
            }

            @Override
            public void onError(Request request, Throwable exception) {
                for (RestyGwtRequestListener requestListener : requestListeners)
                    requestListener.beforeServerErrorHandled(method, request, exception);
                callback.onError(request, exception);
                for (RestyGwtRequestListener requestListener : requestListeners)
                    requestListener.afterServerErrorHandled(method, request, exception);
            }

        });

        for (RestyGwtRequestListener requestListener : requestListeners)
            requestListener.beforeRequestSent(method, builder);
        Request request = builder.send();
        for (RestyGwtRequestListener requestListener : requestListeners)
            requestListener.afterRequestSent(method, builder);
        return request;
    }

    public void addRequestListener(RestyGwtRequestListener requestListener) {
        if (requestListeners == null)
            requestListeners = newArrayList();
        requestListeners.add(requestListener);
    }

}
