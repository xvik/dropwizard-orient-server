package ru.vyarus.dropwizard.orient.support

import com.fasterxml.jackson.annotation.JsonProperty
import io.dropwizard.Configuration
import ru.vyarus.dropwizard.orient.configuration.HasOrientServerConfiguration
import ru.vyarus.dropwizard.orient.configuration.OrientServerConfiguration

import javax.validation.Valid
import javax.validation.constraints.NotNull

/**
 * @author Vyacheslav Rusakov 
 * @since 18.08.2014
 */
class TestConfiguration extends Configuration implements HasOrientServerConfiguration {

    @NotNull
    @Valid
    private OrientServerConfiguration orientServer;

    @Override
    OrientServerConfiguration getOrientServerConfiguration() {
        return orientServer
    }

    @JsonProperty("orient-server")
    void setOrientServer(OrientServerConfiguration orientServer) {
        this.orientServer = orientServer
    }
}
