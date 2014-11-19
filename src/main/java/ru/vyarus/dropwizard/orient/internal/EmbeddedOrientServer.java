package ru.vyarus.dropwizard.orient.internal;

import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;
import com.orientechnologies.common.util.OCallable;
import com.orientechnologies.orient.server.OServer;
import com.orientechnologies.orient.server.OServerMain;
import com.orientechnologies.orient.server.network.OServerNetworkListener;
import com.orientechnologies.orient.server.network.protocol.http.ONetworkProtocolHttpAbstract;
import com.orientechnologies.orient.server.network.protocol.http.command.get.OServerCommandGetStaticContent;
import io.dropwizard.lifecycle.Managed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.vyarus.dropwizard.orient.configuration.OrientServerConfiguration;

import java.io.BufferedInputStream;
import java.net.URL;

/**
 * Orient server managed object. Lifecycle must be managed by dropwizard.
 * Server will be activated only when 'server' command used (jetty manage lifecycle of Managed objects).
 * <p>If static handler registered, registers orient studio.
 * Studio available on url: http://localhost:2480/studio/index.html</p>
 */
public class EmbeddedOrientServer implements Managed {
    private final Logger logger = LoggerFactory.getLogger(EmbeddedOrientServer.class);

    private final OrientServerConfiguration conf;

    /**
     * @param conf orient server configuration object
     */
    public EmbeddedOrientServer(final OrientServerConfiguration conf) {
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


        // install studio (available on url http://localhost:2480/studio/index.html)
        final OServerNetworkListener httpListener = server.getListenerByProtocol(ONetworkProtocolHttpAbstract.class);
        final OServerCommandGetStaticContent command = (OServerCommandGetStaticContent) httpListener
                .getCommand(OServerCommandGetStaticContent.class);

        if (command != null) {
            registerStudio(command);
        }
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

    private void registerStudio(final OServerCommandGetStaticContent command) {
        logger.debug("Registering studio application");
        command.registerVirtualFolder("studio", new OCallable<Object, String>() {
            @Override
            public Object call(final String iArgument) {
                final String fileName = "/ru/vyarus/dropwizard/orient/studio/"
                        + MoreObjects.firstNonNull(Strings.emptyToNull(iArgument), "index.html");
                final URL url = getClass().getResource(fileName);
                if (url != null) {
                    final OServerCommandGetStaticContent.OStaticContent content =
                            new OServerCommandGetStaticContent.OStaticContent();
                    content.is = new BufferedInputStream(getClass().getResourceAsStream(fileName));
                    content.contentSize = -1;
                    content.type = OServerCommandGetStaticContent.getContentType(url.getFile());
                    return content;
                }
                return null;
            }
        });
    }
}
