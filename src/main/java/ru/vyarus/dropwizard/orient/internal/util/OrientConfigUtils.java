package ru.vyarus.dropwizard.orient.internal.util;

import com.orientechnologies.orient.server.config.*;
import com.orientechnologies.orient.server.network.OServerNetworkListener;
import com.orientechnologies.orient.server.network.OServerSSLSocketFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static com.google.common.collect.MoreCollectors.onlyElement;

/**
 * Orient configuration object  utilities.
 *
 * @author Vyacheslav Rusakov
 * @since 27.08.2017
 */
public final class OrientConfigUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrientConfigUtils.class);

    private static final List<String> KEYSTORE_KEYS = Arrays.asList(
            OServerSSLSocketFactory.PARAM_NETWORK_SSL_KEYSTORE,
            OServerSSLSocketFactory.PARAM_NETWORK_SSL_TRUSTSTORE);

    private static final String SOCKET_DEFAULT = "default";

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
                    if (KEYSTORE_KEYS.contains(p.name) && p.value != null) {
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
                final OServerNetworkListenerConfiguration listener = findListenerByPort(network, port);
                return isSslEnabledForListener(network, listener);
            } catch (Exception ex) {
                LOGGER.warn("Failed to check ssl on port " + port, ex);
            }
        }
        return false;
    }

    /**
     * @param network network configuration object
     * @param port    port to search by
     * @return listener handling provided port, otherwise exception is thrown
     */
    public static OServerNetworkListenerConfiguration findListenerByPort(
            final OServerNetworkConfiguration network, final int port) {
        return network.listeners.stream()
                .filter(l -> {
                    for (int rPort : OServerNetworkListener.getPorts(l.portRange)) {
                        if (rPort == port) {
                            return true;
                        }
                    }
                    return false;
                }).collect(onlyElement());
    }

    /**
     * Checks socket implementation class to be derivative from {@link OServerSSLSocketFactory}.
     * But it will not be able to detect completely new implementation (which is very unlikely).
     *
     * @param network  network configuration object
     * @param listener listener configuration object
     * @return true if ssl is enabled, false otherwise (but may throw an exception in case of bad configuration)
     */
    public static boolean isSslEnabledForListener(final OServerNetworkConfiguration network,
                                                  final OServerNetworkListenerConfiguration listener) {

        if (!SOCKET_DEFAULT.equals(listener.socket)) {
            try {
                // find one or throw exception otherwise
                final OServerSocketFactoryConfiguration socket = network.sockets.stream()
                        .filter(s -> s.name.equals(listener.socket)).collect(onlyElement());

                final Class impl = Class.forName(socket.implementation, false,
                        OServerSSLSocketFactory.class.getClassLoader());
                return OServerSSLSocketFactory.class.isAssignableFrom(impl);
            } catch (Exception ex) {
                LOGGER.warn("Failed to check ssl for listener " + listener.protocol, ex);
            }
        }
        return false;
    }
}
