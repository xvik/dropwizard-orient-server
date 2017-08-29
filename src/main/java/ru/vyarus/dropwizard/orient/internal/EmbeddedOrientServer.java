package ru.vyarus.dropwizard.orient.internal;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.io.Files;
import com.orientechnologies.orient.core.config.OGlobalConfiguration;
import com.orientechnologies.orient.server.OServer;
import com.orientechnologies.orient.server.OServerMain;
import com.orientechnologies.orient.server.config.OServerConfiguration;
import com.orientechnologies.orient.server.network.OServerNetworkListener;
import com.orientechnologies.orient.server.network.protocol.binary.ONetworkProtocolBinary;
import com.orientechnologies.orient.server.network.protocol.http.ONetworkProtocolHttpAbstract;
import com.orientechnologies.orient.server.network.protocol.http.command.get.OServerCommandGetStaticContent;
import io.dropwizard.lifecycle.Managed;
import io.dropwizard.server.ServerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.vyarus.dropwizard.orient.configuration.OrientServerConfiguration;
import ru.vyarus.dropwizard.orient.internal.cmd.ApiRedirectCommand;
import ru.vyarus.dropwizard.orient.internal.util.AutoSslConfigurator;
import ru.vyarus.dropwizard.orient.internal.util.OrientConfigUtils;

import java.io.File;
import java.io.IOException;

/**
 * Orient server managed object. Lifecycle must be managed by dropwizard.
 * Server will be activated only when 'server' command used (jetty manage lifecycle of Managed objects).
 * <p>
 * User 'root' must be defined in configuration, otherwise orient will always ask for root user password on start
 * (it would not be able to store password somewhere). As a side effect default guest user would not be created
 * (but you can define it in config).
 * <p>
 * If static handler registered, registers orient studio.
 * Studio available on url: http://localhost:2480/studio/
 * <p>
 * If orient config contains ssl socket configuration: check if configured keystore paths are relative and,
 * if keystore file exists relatively to application start dir, replace config with absolute file path.
 * By default orient will look relatively to orient home. But most likely, the same keystore would be configured for
 * both orient and dropwizard and it may be declared relatively.
 */
public class EmbeddedOrientServer implements Managed {
    private final Logger logger = LoggerFactory.getLogger(EmbeddedOrientServer.class);

    private final OrientServerConfiguration conf;
    private final ObjectMapper mapper;
    private final ServerFactory dwServer;
    private final Info serverInfo = new Info();

    /**
     * @param conf          orient server configuration object
     * @param mapper        for serializing orient security json from yaml configuration
     * @param serverFactory dropwizard connectors configuration
     */
    public EmbeddedOrientServer(final OrientServerConfiguration conf, final ObjectMapper mapper,
                                final ServerFactory serverFactory) {
        this.conf = validateConfiguration(conf);
        this.mapper = mapper;
        this.dwServer = serverFactory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start() throws Exception {
        System.setProperty("ORIENTDB_HOME", conf.getFilesPath());
        System.setProperty("orientdb.www.path", "");
        prepareSecurityConfig();
        if (conf.isAutoSsl()) {
            new AutoSslConfigurator(dwServer, conf.getConfig()).configure();
        }
        OrientConfigUtils.checkLocalFilesInSslSockets(conf.getConfig());

        final OServer server = OServerMain.create();
        server.startup(conf.getConfig()).activate();

        final OServerNetworkListener httpListener = server.getListenerByProtocol(ONetworkProtocolHttpAbstract.class);
        boolean studioInstalled = false;
        if (httpListener != null) {
            final OServerCommandGetStaticContent command = (OServerCommandGetStaticContent) httpListener
                    .getCommand(OServerCommandGetStaticContent.class);
            if (command != null) {
                studioInstalled = new OrientStudioInstaller(command).install();
                httpListener.registerStatelessCommand(new ApiRedirectCommand());
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

    private OrientServerConfiguration validateConfiguration(final OrientServerConfiguration conf) {
        Preconditions.checkNotNull(conf, "Configuration object required");
        Preconditions.checkNotNull(conf.getConfig(), "Orient server configuration required");
        Preconditions.checkState(OrientConfigUtils.hasRootUser(conf.getConfig()),
                "User '%s' must be defined in configuration because otherwise orient will ask "
                        + "for user password on each application start.", OServerConfiguration.DEFAULT_ROOT_USER);
        return conf;
    }

    private void prepareSecurityConfig() {
        // note: in both cases configured file could be overridden with system property. hope nobody will define it
        if (conf.getSecurity() != null) {
            // use the same path as default
            final String file = conf.getFilesPath() + "/config/security.json";
            try {
                final String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(conf.getSecurity());
                final File security = new File(file);
                Files.createParentDirs(security);
                Files.write(json, security, Charsets.UTF_8);
            } catch (IOException e) {
                throw new IllegalStateException("Failed to write orient security file: " + file, e);
            }
            // register path just in case default will change
            OGlobalConfiguration.SERVER_SECURITY_FILE.setValue(file);
            conf.getSecurity().textValue();
            logger.debug("Orient security configured with file: {}", file);
        } else if (conf.getSecurityFile() != null) {
            OGlobalConfiguration.SERVER_SECURITY_FILE.setValue(conf.getSecurityFile());
            logger.debug("Orient security file: {}", conf.getSecurityFile());
        }
    }

    private void fillServerInfo(final OServer server, final boolean studioInstalled) {
        serverInfo.studioInstalled = studioInstalled;
        final OServerNetworkListener httpListener = server.getListenerByProtocol(ONetworkProtocolHttpAbstract.class);
        if (httpListener != null) {
            final int port = httpListener.getInboundAddr().getPort();
            serverInfo.httpPort = String.valueOf(port);
            serverInfo.https = OrientConfigUtils.isSslEnabledOnPort(conf.getConfig(), port);
        }
        final OServerNetworkListener binaryListener = server.getListenerByProtocol(ONetworkProtocolBinary.class);
        if (binaryListener != null) {
            final int port = binaryListener.getInboundAddr().getPort();
            serverInfo.binaryPort = String.valueOf(port);
            serverInfo.binarySsl = OrientConfigUtils.isSslEnabledOnPort(conf.getConfig(), port);
        }
    }

    /**
     * Server installation info.
     */
    @SuppressWarnings("checkstyle:VisibilityModifier")
    public static class Info {
        public boolean studioInstalled;
        /**
         * Https configured for rest and studio (http listener).
         */
        public boolean https;
        public String httpPort;
        /**
         * Ssl configured for binary protocol (binary listener).
         */
        public boolean binarySsl;
        public String binaryPort;
    }
}
