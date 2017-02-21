package ru.vyarus.dropwizard.orient.internal;

import com.orientechnologies.orient.server.network.protocol.http.command.get.OServerCommandGetStaticContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.vyarus.dropwizard.orient.internal.cmd.StudioVirtualFolder;

import java.io.IOException;
import java.net.URL;

import static ru.vyarus.dropwizard.orient.internal.cmd.StudioVirtualFolder.STUDIO_INDEX;
import static ru.vyarus.dropwizard.orient.internal.cmd.StudioVirtualFolder.STUDIO_PATH;

/**
 * Installs studio if studio jar is available on classpath (searches for 'www/index.html' in classpath,
 * assuming it's a studio).
 * Studio is available on url http://localhost:2480/studio/.
 *
 * @author Vyacheslav Rusakov
 * @since 21.08.2015
 */
public class OrientStudioInstaller {


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
        command.registerVirtualFolder("studio", new StudioVirtualFolder());
    }

    private boolean detect() throws IOException {
        final URL studioIndex = getClass().getResource(STUDIO_PATH + STUDIO_INDEX);
        return studioIndex != null;
    }
}
