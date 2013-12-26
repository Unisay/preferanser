package com.preferanser.server.guice;

import com.google.inject.Scopes;
import com.google.inject.servlet.ServletModule;

public class ServerModule extends ServletModule {

    @Override protected void configureServlets() {
        // bind resource classes here

/*        // hook Jersey into Guice Servlet
        bind(GuiceContainer.class);

        // hook Jackson into Jersey as the POJO <-> JSON mapper
        bind(JacksonJsonProvider.class).in(Scopes.SINGLETON);

        serve("*//*").with(GuiceContainer.class);*/

    }

}
