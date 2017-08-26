package ru.vyarus.dropwizard.orient.internal.util;

import com.orientechnologies.orient.server.config.*;
import com.orientechnologies.orient.server.network.OServerNetworkListener;
import com.orientechnologies.orient.server.network.OServerSSLSocketFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Arrays;

import static com.google.common.collect.MoreCollectors.onlyElement;

/**
 * Orient configuration object  utilities.
 *
 * @author Vyacheslav Rusakov
 * @since 27.08.2017
 */
public final class OrientConfigUtils {

    private static final String PARAM_SSL_KEY_STORE = "network.ssl.keyStore";
    private static final String PARAM_SSL_TRUST_STORE = "network.ssl.trustStore";

    private static final Logger LOGGER = LoggerFactory.getLogger(OrientConfigUtils.class);

    private OrientConfigUtils() {
    }

    /**
     * Orient will ask for root user password if root user is not configured. So it's important to configured it
     * in embedded mode.
     *
     * @param conf configuration object
     * @return true if root user configured, false otherwise
     */
    public static boolean hasRootUser(final OServerConfiguration conf) {
        if (conf.users == null) {
            return false;
        }
        boolean res = false;
        for (OServerUserConfiguration user : conf.users) {
            if (user.name.equals(OServerConfiguration.DEFAULT_ROOT_USER)) {
                res = true;
                break;
            }
        }
        return res;
    }

    /**
     * By default, ssl socket configuration assume ORIENTDB_HOME as root for relative files.
     * In context of dropwizard setup, keystore could be located inside app instead.
     * To properly support relative paths, this method checks ssl sockets for configured  relative files
     * and, if they exists, replace it to absolute file path (otherwise orient will look for file relative
     * to it's home).
     *
     * @param conf configuration object
     */
    public static void checkLocalFilesInSslSockets(final OServerConfiguration conf) {
        final OServerNetworkConfiguration network = conf.network;
        if (network.sockets == null || network.sockets.isEmpty()) {
            return;
        }
        network.sockets.forEach(s -> {
            if (s.parameters != null) {
                Arrays.stream(s.parameters).forEach(p -> {
                    if (PARAM_SSL_KEY_STORE.equals(p.name)
                            || PARAM_SSL_TRUST_STORE.equals(p.name)) {
                        final File ks = new File(p.value);
                        if (ks.exists()) {
                            // replace with absolute path to avoid lookup in orient home
                            p.value = ks.getAbsolutePath();
                        }
                    }
                });
            }
        });
    }

    /**
     * Using configuration to detect listener socket. To 100% correctly associate listener instance
     * with configuration listeners use real assigned port as it should match port range from the configuration.
     *
     * @param conf configuration object
     * @param port listener instance port
     * @return true if listener is configured with ssl socket
     */
    public static boolean isSslEnabledOnPort(final OServerConfiguration conf, final int port) {
        final OServerNetworkConfiguration network = conf.network;
        // no custom sockets - only default could be used
        if (network.sockets != null && !network.sockets.isEmpty()) {
            try {
                final Class impl = getSocketImplementationForPort(network, port);
                if (impl != null) {
                    return OServerSSLSocketFactory.class.isAssignableFrom(impl);
                }
            } catch (Exception ex) {
                LOGGER.warn("Failed to check ssl on port " + port, ex);
            }
        }
        return false;
    }

    private static Class getSocketImplementationForPort(final OServerNetworkConfiguration network, final int port)
            throws ClassNotFoundException {
        final OServerNetworkListenerConfiguration listener = network.listeners.stream()
                .filter(l -> {
                    for (int rPort : OServerNetworkListener.getPorts(l.portRange)) {
                        if (rPort == port) {
                            return true;
                        }
                    }
                    return false;
                }).collect(onlyElement());

        if ("default".equals(listener.socket)) {
            return null;
        }
        // find one or throw exception otherwise
        final OServerSocketFactoryConfiguration socket = network.sockets.stream()
                .filter(s -> s.name.equals(listener.socket)).collect(onlyElement());

        // such check would not detect completely new ssl socket implementation,
        // but such case is very unlikely to happen
        return Class.forName(socket.implementation, false,
                OServerSSLSocketFactory.class.getClassLoader());
    }
}
