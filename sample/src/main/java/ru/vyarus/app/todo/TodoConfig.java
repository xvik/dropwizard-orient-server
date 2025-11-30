package ru.vyarus.app.todo;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.core.Configuration;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import ru.vyarus.dropwizard.orient.configuration.OrientServerConfiguration;

/**
 * @author Vyacheslav Rusakov
 * @since 22.11.2025
 */
@Getter
public class TodoConfig extends Configuration {

    @Valid @NotNull
    private DbConfiguration db;

    @Valid @NotNull
    @JsonProperty("orient-server")
    private OrientServerConfiguration orientServer;

    @Getter
    public static class DbConfiguration {
        private String uri;
        private String user;
        private String pass;
    }
}
