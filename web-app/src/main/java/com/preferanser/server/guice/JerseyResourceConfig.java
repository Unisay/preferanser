package com.preferanser.server.guice;

import com.preferanser.server.resource.AuthResource;
import com.sun.jersey.api.core.PackagesResourceConfig;

public class JerseyResourceConfig extends PackagesResourceConfig {

    public JerseyResourceConfig() {
        super(AuthResource.class.getPackage().getName());
    }

}
