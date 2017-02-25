package ru.vyarus.dropwizard.orient.internal;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.io.Files;
import com.orientechnologies.orient.core.config.OGlobalConfiguration;
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
import ru.vyarus.dropwizard.orient.internal.cmd.ApiRedirectCommand;

import java.io.File;
import java.io.IOException;

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
    private final ObjectMapper mapper;
    private final Info serverInfo = new Info();

    /**
     * @param conf   orient server configuration object
     * @param mapper for serializing orient security json from yaml configuration
     */
    public EmbeddedOrientServer(final OrientServerConfiguration conf, final ObjectMapper mapper) {
        this.conf = validateConfiguration(conf);
        this.mapper = mapper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start() throws Exception {
        System.setProperty("ORIENTDB_HOME", conf.getFilesPath());
        System.setProperty("orientdb.www.path", "");
        prepareSecurityConfig();

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
        Preconditions.checkState(hasRootUser(conf.getConfig()),
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
