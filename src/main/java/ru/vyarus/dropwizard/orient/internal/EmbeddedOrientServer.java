package ru.vyarus.dropwizard.orient.internal;

import com.google.common.base.Preconditions;
import com.orientechnologies.orient.server.OServer;
import com.orientechnologies.orient.server.OServerMain;
import com.orientechnologies.orient.server.config.OServerConfiguration;
import com.orientechnologies.orient.server.config.OServerUserConfiguration;
import com.orientechnologies.orient.server.network.OServerNetworkListener;
import com.orientechnologies.orient.server.network.protocol.binary.ONetworkProtocolBinary;
import com.orientechnologies.orient.server.network.protocol.http.ONetworkProtocolHttpAbstract;
import com.orientechnologies.orient.server.network.protocol.http.command.get.OServerCommandGetStaticContent;
import io.dropwizard.lifecycle.Managed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.vyarus.dropwizard.orient.configuration.OrientServerConfiguration;

/**
 * Orient server managed object. Lifecycle must be managed by dropwizard.
 * Server will be activated only when 'server' command used (jetty manage lifecycle of Managed objects).
 * <p>User 'root' must be defined in configuration, otherwise orient will always ask for root user password on start
 * (it would not be able to store password somewhere). As a side effect default guest user would not be created
 * (but you can define it in config).</p>
 * <p>If static handler registered, registers orient studio.
 * Studio available on url: http://localhost:2480/studio/</p>
 */
public class EmbeddedOrientServer implements Managed {
    private final Logger logger = LoggerFactory.getLogger(EmbeddedOrientServer.class);

    private final OrientServerConfiguration conf;
    private final Info serverInfo = new Info();

    /**
     * @param conf orient server configuration object
     */
    public EmbeddedOrientServer(final OrientServerConfiguration conf) {
        validateConfiguration(conf);
        this.conf = conf;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start() throws Exception {
        System.setProperty("ORIENTDB_HOME", conf.getFilesPath());
        System.setProperty("orientdb.www.path", "");
        final OServer server = OServerMain.create();
        server.startup(conf.getConfig()).activate();
        boolean studioInstalled = false;

        final OServerNetworkListener httpListener = server.getListenerByProtocol(ONetworkProtocolHttpAbstract.class);
        if (httpListener != null) {
            final OServerCommandGetStaticContent command = (OServerCommandGetStaticContent) httpListener
                    .getCommand(OServerCommandGetStaticContent.class);
            if (command != null) {
                studioInstalled = new OrientStudioInstaller(command).install();
            }
        }
        fillServerInfo(server, studioInstalled);
        logger.info("Orient server started");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop() throws Exception {
        OServerMain.server().shutdown();
        logger.info("Orient server stopped");
    }

    /**
     * @return server installation information
     */
    public Info getServerInfo() {
        return serverInfo;
    }

    private void validateConfiguration(final OrientServerConfiguration conf) {
        Preconditions.checkNotNull(conf, "Configuration object required");
        Preconditions.checkNotNull(conf.getConfig(), "Orient server configuration required");
        Preconditions.checkState(hasRootUser(conf.getConfig()),
                "User '%s' must be defined in configuration because otherwise orient will ask "
                        + "for user password on each application start.", OServerConfiguration.DEFAULT_ROOT_USER);
    }

    private boolean hasRootUser(final OServerConfiguration conf) {
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

    private void fillServerInfo(final OServer server, final boolean studioInstalled) {
        serverInfo.studioInstalled = studioInstalled;
        final OServerNetworkListener httpListener = server.getListenerByProtocol(ONetworkProtocolHttpAbstract.class);
        if (httpListener != null) {
            serverInfo.httpPort = String.valueOf(httpListener.getInboundAddr().getPort());
        }
        final OServerNetworkListener binaryListener = server.getListenerByProtocol(ONetworkProtocolBinary.class);
        if (binaryListener != null) {
            serverInfo.binaryPort = String.valueOf(binaryListener.getInboundAddr().getPort());
        }
    }

    /**
     * Server installation info.
     */
    @SuppressWarnings("checkstyle:VisibilityModifier")
    public static class Info {
        public boolean studioInstalled;
        public String httpPort;
        public String binaryPort;
    }
}
