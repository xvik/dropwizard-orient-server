package ru.vyarus.dropwizard.orient.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.orientechnologies.common.parser.OSystemVariableResolver;
import com.orientechnologies.orient.server.config.OServerConfiguration;
import com.orientechnologies.orient.server.config.OServerConfigurationLoaderXml;
import com.orientechnologies.orient.server.security.ODefaultServerSecurity;
import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

/**
 * Orient configuration object.
 * Defines database files storage path and orient server configuration.
 * Server configuration could be provided inside yaml file (yaml representation of xml format)
 * or using external xml configuration file (native orient format).
 * <p>
 * Orient security configuration (security.json) may be declared with yaml in "security:" section or with
 * file path (security-file). May work without security config (but with warning in log).
 * <p>
 * Server start could be disabled with 'start' option.
 *
 * @see <a href="http://orientdb.com/docs/last/DB-Server.html#configuration">configuration documentation</a>
 */
public class OrientServerConfiguration {
    private static final String APP_HOME = Paths.get(".").toAbsolutePath().normalize().toString();
    private static final String TMP = System.getProperty("java.io.tmpdir");

    private final Logger logger = LoggerFactory.getLogger(OrientServerConfiguration.class);

    @NotEmpty
    private String filesPath;
    private boolean start = true;
    private String configFile;
    private boolean adminServlet = true;
    @NotNull
    private OServerConfiguration config;

    private JsonNode security;
    private String securityFile;

    /**
     * @return true if orient server must be started, false to avoid starting orient server
     */
    public boolean isStart() {
        return start;
    }

    /**
     * @param start true to start server, false to avoid starting
     */
    public void setStart(final boolean start) {
        this.start = start;
    }

    /**
     * @return path to database files storage
     */
    public String getFilesPath() {
        return filesPath;
    }

    /**
     * Directory may not exist - orient will create it when necessary.
     * Any system property or environment variable may be used with ${prop} syntax.
     * Special variables: $TMP for ${java.io.tmpdir}, $APP_HOME for application starting directory.
     *
     * @param filesPath path to store database files.
     */
    @JsonProperty("files-path")
    public void setFilesPath(final String filesPath) {
        this.filesPath = parsePath(filesPath);
    }

    /**
     * As an alternative to inline yaml configuration, external xml file could be used (but not both).
     * Any system property or environment variable may be used with ${prop} syntax.
     * Special variables: $TMP for ${java.io.tmpdir}, $FILES_HOME for configured files path and
     * $APP_HOME for application starting directory.
     *
     * @param configFile path to server xml configuration file
     */
    @JsonProperty("config-file")
    public void setConfigFile(final String configFile) {
        this.configFile = parsePath(configFile);
        Preconditions.checkState(this.config == null,
                "Orient configuration already declared manually. "
                        + "Use either xml file or direct yaml config, but not both.");
        this.config = parseXmlConfigFile(configFile);
    }

    /**
     * @return true to deploy orient info servlet (/orient) on admin context, false to avoid installing
     */
    public boolean isAdminServlet() {
        return adminServlet;
    }

    /**
     * @param adminServlet true to start orient info servlet on admin context, false to avoid installation
     */
    @JsonProperty("admin-servlet")
    public void setAdminServlet(final boolean adminServlet) {
        this.adminServlet = adminServlet;
    }

    /**
     * @return orient server configuration object (from yaml config or external xml file)
     */
    public OServerConfiguration getConfig() {
        return config;
    }

    /**
     * @param config configuration object defined in yaml configuration file
     */
    public void setConfig(final OServerConfiguration config) {
        Preconditions.checkState(this.config == null,
                "Orient configuration already loaded from file '" + configFile
                        + "'. Use either xml file or direct yaml config, but not both.");
        this.config = config;
    }

    /**
     * @param security orient security definition
     */
    public void setSecurity(final JsonNode security) {
        Preconditions.checkState(this.securityFile == null,
                "Orient security configuration already defined as file '" + securityFile
                        + "'. Use either json file or direct yaml config, but not both.");
        this.security = security;
    }

    /**
     * Orient 2.2 and above provides advanced security configuration. In distribution, this is the file is
     * "config/security.json". It could be loaded only as separate file (not part of main configuration).
     * <p>
     * Optional - orient server will work without it, but print error message about missed file.
     * <p>
     * Security config may be declared inside yaml configuration under "security:" section. Write security json
     * in yaml format. Later it would be stored as json file and configured for orient automatically.
     * <p>
     * NOTE: it is not required to specify security only in yaml. You can ignore this section and use separate json
     * file. You may use "server.security.file" system property to configure it's location or place file inside
     * your files path (files-path property) as "${files/path}/config/security.json.
     *
     * @return security configuration
     * @see <a href="http://orientdb.com/docs/2.2/Security-Config.html">orient securty config</a>
     * @see ODefaultServerSecurity#onBeforeActivate() for details about configuration loading
     */
    public JsonNode getSecurity() {
        return security;
    }

    /**
     * If orient security file (security.json) is not provided with security property in yaml, then it may be
     * specified as path to security file (but not both!).
     * Special variables: $TMP for ${java.io.tmpdir}, $FILES_HOME for configured files path and
     * $APP_HOME for application starting directory.
     *
     * @param securityFile path to security file
     */
    @JsonProperty("security-file")
    public void setSecurityFile(final String securityFile) {
        Preconditions.checkState(this.security == null, "Orient security already defined in yaml. "
                + "Use either json file or yaml, but not both.");
        this.securityFile = parsePath(securityFile);
    }

    /**
     * @return orient security file path
     */
    public String getSecurityFile() {
        return securityFile;
    }

    private OServerConfiguration parseXmlConfigFile(final String configFile) {
        logger.info("Loading orient configuration from file {}", configFile);
        final OServerConfigurationLoaderXml configurationLoader =
                new OServerConfigurationLoaderXml(OServerConfiguration.class, new File(configFile));
        try {
            return configurationLoader.load();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load configuration from file: " + configFile, e);
        }
    }

    private String parsePath(final String path) {
        String trimmedPath = Strings.emptyToNull(path);
        if (trimmedPath != null) {
            trimmedPath = trimmedPath.replace("$TMP", TMP);
            if (filesPath != null) {
                trimmedPath = trimmedPath.replace("$FILES_HOME", filesPath);
            }
            trimmedPath = trimmedPath.replace("$APP_HOME", APP_HOME);
            trimmedPath = OSystemVariableResolver.resolveSystemVariables(trimmedPath);
        }
        return trimmedPath;
    }
}
