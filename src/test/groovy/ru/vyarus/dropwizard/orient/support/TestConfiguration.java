package ru.vyarus.dropwizard.orient.support;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.core.Configuration;
import ru.vyarus.dropwizard.orient.configuration.OrientServerConfiguration;

import jakarta.validation.Valid;

/**
 * Groovy class can't be used for configuraton anymore, because jersey 2.5 is very sensible for addition methods
 *
 * @author Vyacheslav Rusakov
 * @since 18.08.2014
 */
public class TestConfiguration extends Configuration {

    public OrientServerConfiguration getOrientServerConfiguration() {
        return orientServer;
    }

    @JsonProperty("orient-server")
    public void setOrientServer(OrientServerConfiguration orientServer) {
        this.orientServer = orientServer;
    }

    @Valid
    private OrientServerConfiguration orientServer;
    @JsonProperty
    private String foo;
}
