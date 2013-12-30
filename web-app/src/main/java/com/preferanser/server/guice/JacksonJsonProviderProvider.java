package com.preferanser.server.guice;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.google.inject.Provider;

/**
 * Provider of provider ;)
 */
public class JacksonJsonProviderProvider implements Provider<JacksonJsonProvider> {

    private JacksonJsonProvider provider;

    public JacksonJsonProviderProvider() {
        provider = new JacksonJsonProvider();
        provider.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    @Override
    public JacksonJsonProvider get() {
        return provider;
    }

}
