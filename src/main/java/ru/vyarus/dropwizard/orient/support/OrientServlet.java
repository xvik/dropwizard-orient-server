package ru.vyarus.dropwizard.orient.support;

import com.orientechnologies.orient.core.OConstants;
import com.orientechnologies.orient.core.config.OContextConfiguration;
import com.orientechnologies.orient.server.OServerMain;
import com.orientechnologies.orient.server.plugin.OServerPluginInfo;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import ru.vyarus.dropwizard.orient.internal.EmbeddedOrientServer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Embedded orient server info servlet. Deployed on '/orient' path in admin context
 * (by default 'http://localhost:8081/orient').
 * <p>
 * Special url '/orient/studio' leads to embedded studio (no matter what port was configured
 * it will redirect properly).
 * <p>
 * Servlet is installed only when embedded server is started. Servlet deployment could be disabled
 * in configuration ('admin-servlet' option).
 *
 * @author Vyacheslav Rusakov
 * @since 25.08.2015
 */
public class OrientServlet extends HttpServlet {

    public static final String STUDIO_URI = "/studio";

    private static final long serialVersionUID = -2850794040708785320L;
    private static final String CONF_PROFILER = "profiler.enabled";

    //CHECKSTYLE:OFF
    private static final String TEMPLATE = String.format(
            "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\"%n"
                    + "        \"http://www.w3.org/TR/html4/loose.dtd\">%n"
                    + "<html>%n"
                    + "<head>%n"
                    + "  <title>OrientDB</title>%n"
                    + "</head>%n"
                    + "<body>%n"
                    + "  <h1>Embedded OrientDB server</h1>%n"
                    + "  <ul>%n"
                    + "    <li>Version: {0}</li>%n"
                    + "    <li>Binary ports: {1}</li>%n"
                    + "    <li>Http ports: {2}</li>%n"
                    + "    <li>Studio installed: {3}</li>%n"
                    + "    <li>Plugins: {4}</li>%n"
                    + "    <li>Profiler: {7}</li>%n"
                    + "  </ul>%n"
                    + "  {8}%n"
                    + "</body>%n"
                    + "</html>"
    );

    private static final String LINKS_TEMPLATE = String.format(
            "  <h2>Links</h2>%n"
                    + "  <ul>%n"
                    + "    <li><a href=\"{0}/studio\">Embedded studio</a></li>%n"
                    + "  </ul>%n"
                    + "  <ul>%n"
                    + "    <li><a href=\"{1}\" {2}>Documentation</a>%n"
                    + "    <ul>%n"
                    + "        <li><a href=\"{1}internals/DB-Server.html#configuration\" {2}>Server</a></li>%n"
                    + "        <li><a href=\"{1}sql/SQL-Commands.html\" {2}>Sql commands</a></li>%n"
                    + "        <li><a href=\"{1}sql/SQL-Functions.html#bundled-functions\" {2}>Sql functions</a></li>%n"
                    + "        <li><a href=\"{1}sql/SQL-Methods.html\" {2}>Sql methods</a></li>%n"
                    + "        <li><a href=\"{1}gettingstarted/demodb/queries/\" {2}>Query examples</a></li>%n"
                    + "        <li><a href=\"{1}console/Console-Commands.html\" {2}>Console commands</a></li>%n"
                    + "        <li><a href=\"{1}misc/OrientDB-REST.html\" {2}>Rest api</a></li>%n"
                    + "    </ul></li>%n"
                    + "  </ul>"
    );
    //CHECKSTYLE:ON

    private final transient EmbeddedOrientServer.Info info;

    public OrientServlet(final EmbeddedOrientServer.Info info) {
        this.info = info;
    }

    @Override
    protected void service(final HttpServletRequest req, final HttpServletResponse resp)
            throws ServletException, IOException {
        final String uri = req.getPathInfo();
        // if webjar is not used, studio could still be installed as dynamic plugin (in db files folder)
        if (STUDIO_URI.equals(uri)) {
            resp.sendRedirect(String.format("%s://%s:%s/studio/", info.httpSecured ? "https" : "http",
                    req.getServerName(), info.httpPort));
        } else {
            super.service(req, resp);
        }
    }

    @Override
    @SuppressFBWarnings("RCN_REDUNDANT_NULLCHECK_WOULD_HAVE_BEEN_A_NPE")
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp)
            throws ServletException, IOException {
        final String path = req.getContextPath() + req.getServletPath();

        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setHeader("Cache-Control", "must-revalidate,no-cache,no-store");
        resp.setContentType("text/html");

        try (PrintWriter writer = resp.getWriter()) {
            final OContextConfiguration config = OServerMain.server().getContextConfiguration();
            writer.println(MessageFormat.format(TEMPLATE,
                    OConstants.getVersion(),
                    renderPorts(info.binaryPorts),
                    renderPorts(info.httpPorts),
                    info.studioInstalled,
                    renderPlugins(),
                    config.getValueAsString(CONF_PROFILER, null),
                    renderLinks(path)));
        }
    }

    private String renderLinks(final String path) {
        return MessageFormat.format(LINKS_TEMPLATE, path,
                "http://orientdb.org/docs/3.0.x/",
                "target=\"_blank\"");
    }

    private String renderPlugins() {
        final StringBuilder installedPlugins = new StringBuilder();

        final Collection<OServerPluginInfo> plugins = OServerMain.server().getPlugins();
        if (!plugins.isEmpty()) {
            installedPlugins.append("<ul>");
            for (OServerPluginInfo plugin : plugins) {
                installedPlugins.append(String.format("<li>%s</li>",
                        plugin.getName()));
            }
            installedPlugins.append("</ul>");
        } else {
            installedPlugins.append("none");
        }
        return installedPlugins.toString();
    }

    private String renderPorts(final Map<String, Boolean> ports) {
        return ports.isEmpty() ? "disabled" : ports.entrySet()
                .stream()
                .map(entry -> entry.getKey() + (entry.getValue() ? " (ssl)" : ""))
                .collect(Collectors.joining(", "));
    }
}
