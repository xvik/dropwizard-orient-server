package ru.vyarus.dropwizard.orient.internal.cmd;

import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;
import com.orientechnologies.common.util.OCallable;
import com.orientechnologies.orient.server.network.protocol.http.command.get.OServerCommandGetStaticContent;

import java.io.BufferedInputStream;
import java.net.URL;

/**
 * Virtual folder for official studio jar.
 *
 * @author Vyacheslav Rusakov
 * @since 21.02.2017
 */
public class StudioVirtualFolder implements OCallable<Object, String> {

    public static final String STUDIO_PATH = "/www/";
    public static final String STUDIO_INDEX = "index.html";

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
}
