package ru.vyarus.dropwizard.orient;

import io.dropwizard.core.Configuration;
import io.dropwizard.core.ConfiguredBundle;
import io.dropwizard.core.setup.Bootstrap;
import io.dropwizard.core.setup.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.vyarus.dropwizard.orient.configuration.OrientServerConfiguration;
import ru.vyarus.dropwizard.orient.configuration.deserializer.EntryDeserializer;
import ru.vyarus.dropwizard.orient.configuration.deserializer.NetworkProtocolDeserializer;
import ru.vyarus.dropwizard.orient.configuration.deserializer.ParameterDeserializer;
import ru.vyarus.dropwizard.orient.health.OrientServerHealthCheck;
import ru.vyarus.dropwizard.orient.internal.EmbeddedOrientServer;
import ru.vyarus.dropwizard.orient.support.ConsoleCommand;
import ru.vyarus.dropwizard.orient.support.OrientServlet;

import java.util.function.Function;

/**
 * Bundle starts embedded orient server.
 * <p>
 * Orient server configuration is completely embedded into dropwizard yaml configuration. It is almost 1-1 mapping
 * from orient xml structure.
 * <p>
 * Server startup could be disabled by setting 'start: false' in config.
 * <p>
 * Also, registers console command for interacting with database.
 * <p>
 * NOTE: server will not start when console command called, because dropwizard will not run managed objects this time
 * (only server command triggers managed objects lifecycle). But plocal connections still could be used.
 * Also, if server already started, then you can use remote connections.
 *
 * @param <T> configuration type
 */
@SuppressWarnings("checkstyle:ClassDataAbstractionCoupling")
public class OrientServerBundle<T extends Configuration> implements ConfiguredBundle<T> {
    private final Logger logger = LoggerFactory.getLogger(OrientServerBundle.class);

    private final Function<T, OrientServerConfiguration> configurationProvider;

    /**
     * @param configurationProvider configuration provider
     */
    public OrientServerBundle(final Function<T, OrientServerConfiguration> configurationProvider) {
        this.configurationProvider = configurationProvider;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public void initialize(final Bootstrap<?> bootstrap) {
        // support shorter configuration
        bootstrap.getObjectMapper().addHandler(new EntryDeserializer());
        bootstrap.getObjectMapper().addHandler(new ParameterDeserializer());
        bootstrap.getObjectMapper().addHandler(new NetworkProtocolDeserializer());

        bootstrap.addCommand(new ConsoleCommand(
                bootstrap.getApplication().getConfigurationClass(),
                configurationProvider));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run(final T configuration, final Environment environment) throws Exception {

        final OrientServerConfiguration conf = configurationProvider.apply(configuration);
        if (conf == null || !conf.isStart()) {
            logger.debug("Orient server start disabled. Set 'start: true' in configuration to enable.");
            return;
        }

        final EmbeddedOrientServer orientServer = new EmbeddedOrientServer(conf, environment.getObjectMapper(),
                configuration.getServerFactory());
        environment.lifecycle().manage(orientServer);
        environment.healthChecks().register("orient-server", new OrientServerHealthCheck());
        if (conf.isAdminServlet()) {
            environment.getAdminContext().addServlet(new OrientServlet(orientServer.getServerInfo()), "/orient/*");
        }
    }
}
