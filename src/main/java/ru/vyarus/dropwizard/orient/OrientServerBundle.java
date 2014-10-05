package ru.vyarus.dropwizard.orient;

import io.dropwizard.Configuration;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.hibernate.validator.internal.engine.ValidatorFactoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.vyarus.dropwizard.orient.configuration.HasOrientServerConfiguration;
import ru.vyarus.dropwizard.orient.configuration.OrientServerConfiguration;
import ru.vyarus.dropwizard.orient.health.OrientServerHealthCheck;
import ru.vyarus.dropwizard.orient.internal.DummyTraversableResolver;
import ru.vyarus.dropwizard.orient.internal.EmbeddedOrientServer;
import ru.vyarus.dropwizard.orient.support.ConsoleCommand;

import java.lang.reflect.Field;

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
 * @param <T> configuration type
 */
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

        environment.lifecycle().manage(new EmbeddedOrientServer(conf));
        environment.healthChecks().register("orient-server", new OrientServerHealthCheck());
    }

    /**
     * Orientdb-object module includes hiberate-jpa-api into classpath and hibernate-validator
     * starts to think that JPA is available (if jpa available then validated objects must be checked with
     * traversable provider to avoid lazy init exceptions).
     * But orient's OJPAPersistenceProvider.getProviderUtil() simply throws exception.
     * So the only way to resolve problem is to substitute traversableProvider with dummy one.
     * It's not a hack: in normal case, validator also use dummy impl and only if
     * javax.persistence.Persistence class found in classpath use complete impl.. so we just correct
     * behaviour here.
     * Note that it can't cause side effects because hibernate is actually not used.
     */
    private void recoverValidatorBehaviour(final Bootstrap<?> bootstrap) {
        logger.debug("Replacing TraversableResolver to fix hibernate validator");
        final ValidatorFactoryImpl factory = (ValidatorFactoryImpl) bootstrap.getValidatorFactory();
        try {
            final Field field = factory.getClass().getDeclaredField("traversableResolver");
            field.setAccessible(true);
            field.set(factory, new DummyTraversableResolver());
            field.setAccessible(false);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to substitute traversableResolver", e);
        }
    }
}
