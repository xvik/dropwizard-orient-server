package ru.vyarus.dropwizard.orient;

import io.dropwizard.Configuration;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.jersey.validation.Validators;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.eclipse.jetty.servlet.ServletHolder;
import org.hibernate.validator.internal.engine.resolver.JPATraversableResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.vyarus.dropwizard.orient.configuration.OrientServerConfiguration;
import ru.vyarus.dropwizard.orient.configuration.deserializer.EntryDeserializer;
import ru.vyarus.dropwizard.orient.configuration.deserializer.NetworkProtocolDeserializer;
import ru.vyarus.dropwizard.orient.configuration.deserializer.ParameterDeserializer;
import ru.vyarus.dropwizard.orient.health.OrientServerHealthCheck;
import ru.vyarus.dropwizard.orient.internal.EmbeddedOrientServer;
import ru.vyarus.dropwizard.orient.internal.TraverseAllResolver;
import ru.vyarus.dropwizard.orient.support.ConsoleCommand;
import ru.vyarus.dropwizard.orient.support.OrientServlet;

import javax.validation.TraversableResolver;
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
 * Important note: orient object database conflicts with hibernate-validator, because orient brings jpa api jar,
 * which activates hibernate lazy properties checks in validator. Bundle implicitly fixes this by overriding
 * {@link javax.validation.ValidatorFactory} in bootstrap. It should not bring any side effects for most cases, but
 * if you declare custom {@link javax.validation.ValidatorFactory} then simply declare {@link TraversableResolver}
 * manually in it to prevent override (e.g. into {@link TraverseAllResolver}).
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

        recoverValidatorBehaviour(bootstrap);
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
            environment.getAdminContext().addServlet(new ServletHolder(
                    new OrientServlet(orientServer.getServerInfo())), "/orient/*");
        }
    }

    /**
     * Orientdb-object module includes hiberate-jpa-api into classpath and hibernate-validator
     * starts to think that JPA is available (if jpa available then validated objects must be checked with
     * traversable provider to avoid lazy init exceptions).
     * But orient's {@code OJPAPersistenceProvider.getProviderUtil()} simply throws exception.
     * <p>
     * The only way to resolve problem is to manually register correct {@link TraversableResolver}.
     * (see {@link org.hibernate.validator.internal.engine.resolver.TraversableResolvers#getDefault()} for
     * default resolver selection logic).
     * <p>
     * It's not a hack: in normal case, validator use the same implementation, so it's only behaviour fix due to
     * additional jpa jar appeared in classpath (validator is wrong assuming it means jpa will be really used).
     * This substitution could only override manually registered factory and to avoid this create this
     * factory with correct resolver.
     * <p>
     * Note that it can't cause side effects because hibernate is actually not used.
     */
    private void recoverValidatorBehaviour(final Bootstrap<?> bootstrap) {
        final TraversableResolver resolver = bootstrap.getValidatorFactory().getTraversableResolver();
        if (isObjectOrientUsed() && resolver instanceof JPATraversableResolver) {
            logger.debug("Overriding incorrectly configured ValidatorFactory to remove jpa support");
            // repalce JPA traversible resolver with default implementation (it would be uset automatically
            // if jpa jar would not appear in classpath
            bootstrap.setValidatorFactory(Validators.newConfiguration()
                    .traversableResolver(new TraverseAllResolver())
                    .buildValidatorFactory());
        }
    }

    /**
     * Hibernate validation hack is requried only if object support is available on classpath.
     * Without it, real jpa may be used together with graph api.
     *
     * @return true if object api detected, false otherwise
     */
    private boolean isObjectOrientUsed() {
        // check whether we have Persistence on the classpath
        try {
            Class.forName("com.orientechnologies.orient.object.db.OObjectDatabaseTx",
                    false, this.getClass().getClassLoader());
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
