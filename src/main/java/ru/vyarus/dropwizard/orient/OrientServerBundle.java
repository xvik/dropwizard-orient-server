package ru.vyarus.dropwizard.orient;

import io.dropwizard.Configuration;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.jersey.validation.Validators;
import io.dropwizard.jetty.NonblockingServletHolder;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.hibernate.validator.internal.engine.resolver.JPATraversableResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.vyarus.dropwizard.orient.configuration.HasOrientServerConfiguration;
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

/**
 * Bundle starts embedded orient server. Application configuration object must implement
 * {@code HasOrientConfiguration}, which provides orient configuration object.
 * <p>In configuration (yaml) orient server configuration (orient own config) could be defined
 * as reference to xml file (orient default format) or directly in main config (by converting xml to yaml).
 * Server startup could be disabled by setting 'start: false' in config.</p>
 * <p>Additionally registers console command.</p>
 * <p>Important note: orient object database conflicts with hibernate-validator, because orient brings jpa classes,
 * which activates hibernate lazy properties checks in validator. Bundle implicitly fixes this by overriding
 * TraversableResolver. It's completely safe for application if you don't use hibernate.</p>
 * NOTE: server will not start when console command called, because dropwizard will not run managed objects this time
 * (only server command triggers managed objects lifecycle). But plocal connections still could be used.
 * Also, if server already started, then you can use remote connections.
 *
 * @param <T> configuration type
 */
@SuppressWarnings("checkstyle:ClassDataAbstractionCoupling")
public class OrientServerBundle<T extends Configuration & HasOrientServerConfiguration>
        implements ConfiguredBundle<T> {
    private final Logger logger = LoggerFactory.getLogger(OrientServerBundle.class);

    private final Class<T> configClass;

    /**
     * @param configClass configuration class
     */
    public OrientServerBundle(final Class<T> configClass) {
        this.configClass = configClass;
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
        bootstrap.addCommand(new ConsoleCommand(configClass));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run(final T configuration, final Environment environment) throws Exception {

        final OrientServerConfiguration conf = configuration.getOrientServerConfiguration();
        if (conf == null || !conf.isStart()) {
            logger.debug("Orient server start disabled. Set 'start: true' in configuration to enable.");
            return;
        }

        final EmbeddedOrientServer orientServer = new EmbeddedOrientServer(conf, environment.getObjectMapper(),
                configuration.getServerFactory());
        environment.lifecycle().manage(orientServer);
        environment.healthChecks().register("orient-server", new OrientServerHealthCheck());
        if (conf.isAdminServlet()) {
            environment.getAdminContext().addServlet(new NonblockingServletHolder(
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
