package ru.vyarus.dropwizard.orient.internal;

import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;
import com.orientechnologies.common.util.OCallable;
import com.orientechnologies.orient.server.network.protocol.http.command.get.OServerCommandGetStaticContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

/**
 * Installs studio if <a href="https://github.com/webjars/orientdb-studio">studio webjar</a> is available on classpath.
 *
 * @author Vyacheslav Rusakov
 * @since 21.08.2015
 */
public class OrientStudioInstaller {
    public static final String STUDIO_WEBJAR_CONFIG =
            "/META-INF/maven/org.webjars/orientdb-studio/pom.properties";

    private final Logger logger = LoggerFactory.getLogger(OrientStudioInstaller.class);

    private final OServerCommandGetStaticContent command;

    public OrientStudioInstaller(final OServerCommandGetStaticContent command) {
        this.command = command;
    }

    public void install() throws Exception {
        final String studioPath = getStudioPackage();
        if (studioPath != null) {
            registerStudio(studioPath);
        }
    }

    private void registerStudio(final String studioPath) throws Exception {
        logger.debug("Registering studio application");
        command.registerVirtualFolder("studio", new OCallable<Object, String>() {
            @Override
            public Object call(final String iArgument) {
                final String fileName = studioPath
                        + MoreObjects.firstNonNull(Strings.emptyToNull(iArgument), "index.htm");
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

    private String getStudioPackage() throws Exception {
        final URL studioConfig = getClass().getResource(STUDIO_WEBJAR_CONFIG);
        String res = null;
        if (studioConfig != null) {
            final Properties props = new Properties();
            try (final InputStream propsStream = getClass().getResourceAsStream(STUDIO_WEBJAR_CONFIG)) {
                props.load(propsStream);
                res = "/META-INF/resources/webjars/orientdb-studio/" + props.getProperty("version") + "/";
            }
        }
        return res;
    }
}
