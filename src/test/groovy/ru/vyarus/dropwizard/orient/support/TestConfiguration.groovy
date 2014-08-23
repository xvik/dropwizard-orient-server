package ru.vyarus.dropwizard.orient.support

import com.fasterxml.jackson.annotation.JsonProperty
import io.dropwizard.Configuration
import ru.vyarus.dropwizard.orient.configuration.HasOrientConfiguration
import ru.vyarus.dropwizard.orient.configuration.OrientConfiguration

import javax.validation.Valid
import javax.validation.constraints.NotNull

/**
 * @author Vyacheslav Rusakov 
 * @since 18.08.2014
 */
class TestConfiguration extends Configuration implements HasOrientConfiguration {

    @NotNull
    @Valid
    private OrientConfiguration orientServer;

    @Override
    OrientConfiguration getOrientConfiguration() {
        return orientServer
    }

    @JsonProperty("orient-server")
    void setOrientServer(OrientConfiguration orientServer) {
        this.orientServer = orientServer
    }
}
