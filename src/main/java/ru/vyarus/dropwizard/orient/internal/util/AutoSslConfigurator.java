package ru.vyarus.dropwizard.orient.internal.util;

import com.orientechnologies.orient.core.config.OGlobalConfiguration;
import com.orientechnologies.orient.server.config.OServerConfiguration;
import com.orientechnologies.orient.server.config.OServerNetworkListenerConfiguration;
import com.orientechnologies.orient.server.config.OServerParameterConfiguration;
import com.orientechnologies.orient.server.config.OServerSocketFactoryConfiguration;
import com.orientechnologies.orient.server.network.OServerTLSSocketFactory;
import io.dropwizard.jetty.ConnectorFactory;
import io.dropwizard.jetty.HttpsConnectorFactory;
import io.dropwizard.server.DefaultServerFactory;
import io.dropwizard.server.ServerFactory;
import io.dropwizard.server.SimpleServerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static com.orientechnologies.orient.server.network.OServerSSLSocketFactory.*;

/**
 * Automatically configures ssl for both binary and http orient listeners when dropwizard main context
 * has ssl configuration. Will do nothing if at least one listener is already configured for ssl.
 * <p>
 * IMPORTANT: this is not intended to be used in production, but for prototyping and local testing
 * (make thirst ssl tests simpler at first). It is always better to configure orient for production manually.
 * Also, note that configuration process is best suited for default orient configuration,
 * <p>
 * When enabled (see auto-ssl configuration option) and main dropwizard context is configured for https
 * (or simple conf):
 * <ul>
 * <li>Ssl socket (OServerTLSSocketFactory) added to orient configuration (network.sockets)</li>
 * <li>All listeners configured to use ssl socket (by default it's binary and http)</li>
 * <li>If binary listener is configured with default ports range (2424-2430) ports then default ssl ports
 * will be configured: 2434-2440. This is done because by default orient will use 2434 port for ssl
 * client connection</li>
 * <li>Enables SSL for orient client: OGlobalConfiguration.CLIENT_USE_SSL.setValue(true). This will make
 * all remote connections use ssl by default. So simple "remote:localhost/db" path will use https
 * automatically.</li>
 * </ul>
 * <p>
 * NOTE: if multiple listener configured all of them will be changed to use ssl. So case when both http and https
 * versions available is not possible with auto-ssl!
 *
 * @author Vyacheslav Rusakov
 * @see <a href="https://orientdb.org/docs/2.2.x/Using-SSL-with-OrientDB.html">docs</a>
 * @since 27.08.2017
 */
public class AutoSslConfigurator {

    public static final String AUTO_SSL_SOCKET = "auto_ssl_from_dw";

    private final Logger logger = LoggerFactory.getLogger(AutoSslConfigurator.class);

    private final ServerFactory dwServer;
    private final OServerConfiguration conf;

    public AutoSslConfigurator(final ServerFactory dwServer, final OServerConfiguration conf) {
        this.dwServer = dwServer;
        this.conf = conf;
    }

    public void configure() {
        if (dwServer instanceof SimpleServerFactory) {
            final SimpleServerFactory factory = (SimpleServerFactory) dwServer;
            final ConnectorFactory connector = factory.getConnector();
            checkAndConfigure(connector);
        } else {
            final DefaultServerFactory factory = (DefaultServerFactory) dwServer;
            for (ConnectorFactory connector : factory.getApplicationConnectors()) {
                checkAndConfigure(connector);
            }
        }
    }

    private void checkAndConfigure(final ConnectorFactory connector) {
        if (connector instanceof HttpsConnectorFactory) {
            final List<OServerSocketFactoryConfiguration> sockets = conf.network.sockets;
            // no defined sockets already mean no ssl configured, otherwise look listeners
            // (sockets may be defined but not actually used)
            if (sockets != null && !sockets.isEmpty() && isSslAlreadyDefined()) {
                logger.warn("Orient auto ssl configuration not performed because ssl socket is defined "
                        + "manually and used in one of the listeners (see network.listeners section)");
            } else {
                applySsl((HttpsConnectorFactory) connector);
            }
        }
    }

    private boolean isSslAlreadyDefined() {
        // looking for listeners configured with ssl socket
        return conf.network.listeners.stream()
                .filter(l -> OrientConfigUtils.isSslEnabledForListener(conf.network, l)).count() > 0;
    }

    private void applySsl(final HttpsConnectorFactory con) {
        if (con.getKeyStoreProvider() != null || con.getTrustStoreProvider() != null) {
            logger.warn("Orient auto ssl configuration is impossible because dropwizard "
                    + "configured using provider");
            return;
        }

        final OServerSocketFactoryConfiguration ssl = new OServerSocketFactoryConfiguration();
        ssl.name = AUTO_SSL_SOCKET;
        ssl.implementation = OServerTLSSocketFactory.class.getName();
        ssl.parameters = buildParameters(con);

        if (conf.network.sockets == null) {
            conf.network.sockets = new ArrayList<>();
        }
        conf.network.sockets.add(ssl);

        // apply ssl for both binary and http
        conf.network.listeners.forEach(this::updateListener);
        // required for remote connections usage (we know for sure that server use ssl only so safe to configure)
        OGlobalConfiguration.CLIENT_USE_SSL.setValue(true);
        logger.info("SSL configuration applied to orient based on dropwizard main context configuration."
                + "Client SSL (OGlobalConfiguration.CLIENT_USE_SSL) enabled.");
    }

    private OServerParameterConfiguration[] buildParameters(final HttpsConnectorFactory con) {
        final List<OServerParameterConfiguration> res = new ArrayList<>();
        addIfSet(res, PARAM_NETWORK_SSL_KEYSTORE, con.getKeyStorePath());
        addIfSet(res, PARAM_NETWORK_SSL_KEYSTORE_TYPE, con.getKeyStoreType());
        addIfSet(res, PARAM_NETWORK_SSL_KEYSTORE_PASSWORD, con.getKeyStorePassword());
        addIfSet(res, PARAM_NETWORK_SSL_TRUSTSTORE, con.getTrustStorePath());
        addIfSet(res, PARAM_NETWORK_SSL_TRUSTSTORE_TYPE, con.getTrustStoreType());
        addIfSet(res, PARAM_NETWORK_SSL_TRUSTSTORE_PASSWORD, con.getTrustStorePassword());
        return res.toArray(new OServerParameterConfiguration[0]);
    }

    private void addIfSet(final List<OServerParameterConfiguration> res, final String key, final String val) {
        if (val != null) {
            res.add(new OServerParameterConfiguration(key, val));
        }
    }

    private void updateListener(final OServerNetworkListenerConfiguration listener) {
        listener.socket = AUTO_SSL_SOCKET;
        // this is important because remote orient connection will use different default port for ssl (2430)
        if ("2424-2430".equals(listener.portRange)) {
            listener.portRange = "2434-2440";
            logger.info("Default orient binary ports 2424-2430 changed to default ssl ports 2434-2440");
        }
    }
}
