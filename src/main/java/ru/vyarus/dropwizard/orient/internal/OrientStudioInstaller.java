package ru.vyarus.dropwizard.orient.internal;

import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;
import com.orientechnologies.common.util.OCallable;
import com.orientechnologies.orient.server.network.protocol.http.command.get.OServerCommandGetStaticContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;

/**
 * Installs studio if studio jar is available on classpath (searches for 'www/index.html' in classpath,
 * assuming it's a studio).
 * Studio is available on url http://localhost:2480/studio/.
 *
 * @author Vyacheslav Rusakov
 * @since 21.08.2015
 */
public class OrientStudioInstaller {
    public static final String STUDIO_PATH = "/www/";
    public static final String STUDIO_INDEX = "index.html";

    private final Logger logger = LoggerFactory.getLogger(OrientStudioInstaller.class);

    private final OServerCommandGetStaticContent command;

    public OrientStudioInstaller(final OServerCommandGetStaticContent command) {
        this.command = command;
    }

    /**
     * Searches for studio webjar and if found installs studio.
     *
     * @return true if studio installed, false otherwise
     * @throws Exception if installation fails
     */
    public boolean install() throws Exception {
        final boolean detected = detect();
        if (detected) {
            registerStudio();
        }
        return detected;
    }

    private void registerStudio() throws Exception {
        logger.debug("Registering studio application");

        command.registerVirtualFolder("studio", new OCallable<Object, String>() {
            @Override
            public Object call(final String iArgument) {
                final String fileName = STUDIO_PATH
                        + MoreObjects.firstNonNull(Strings.emptyToNull(iArgument), STUDIO_INDEX);
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

    private boolean detect() throws IOException {
        final URL studioIndex = getClass().getResource(STUDIO_PATH + STUDIO_INDEX);
        return studioIndex != null;
    }
}
