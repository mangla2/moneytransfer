package com.revolut.app.config;

import javax.ws.rs.ApplicationPath;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

@ApplicationPath("resources")
class AppConfig extends ResourceConfig {

    AppConfig() {
        packages("com.revolut.app");
        register(JacksonFeature.class);
    }
}