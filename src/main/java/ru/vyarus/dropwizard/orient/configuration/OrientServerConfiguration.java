package ru.vyarus.dropwizard.orient.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.orientechnologies.orient.server.config.OServerConfiguration;
import com.orientechnologies.orient.server.config.OServerConfigurationLoaderXml;
import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.IOException;

/**
 * Orient configuration object.
 * Defines database files storage path and orient server configuration.
 * Server configuration could be provided inside yaml file (yaml representation of xml format)
 * or using external xml configuration file (native orient format).
 * <p>Server start could be disabled with 'start' option.</p>
 * <a href="http://www.orientechnologies.com/docs/last/orientdb.wiki/DB-Server.html#configuration">
 * See configuration documentation.</a>
 */
public class OrientServerConfiguration {
    private final Logger logger = LoggerFactory.getLogger(OrientServerConfiguration.class);

    @NotEmpty
    private String filesPath;
    private boolean start = true;
    private String configFile;
    @NotNull
    private OServerConfiguration config;

    /**
     * @return true if server must be started, false otherwise
     */
    public boolean isStart() {
        return start;
    }

    /**
     * @param start true to start server, false to avoid starting
     */
    @JsonProperty
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
     * Special variable '$TMP' could be used. It will be substituted
     * by system temp directory path ('java.io.tmpdir').
     *
     * @param filesPath path to store database files.
     */
    @JsonProperty("files-path")
    public void setFilesPath(final String filesPath) {
        this.filesPath = parseDbPath(filesPath);
    }

    /**
     * As an alternative to inline yaml configuration, external xml file could be used (but not both).
     *
     * @param configFile path to server xml configuration file
     */
    @JsonProperty("config-file")
    public void setConfigFile(final String configFile) {
        this.configFile = configFile;
        this.config = parseXmlConfigFile(configFile);
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
    @JsonProperty
    public void setConfig(final OServerConfiguration config) {
        Preconditions.checkState(this.config == null,
                "Orient configuration already loaded from file '" + configFile
                        + "'. Use either xml file or direct yaml config, but not both.");
        this.config = config;
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

    private String parseDbPath(final String path) {
        final String trimmedPath = Strings.emptyToNull(path);
        return trimmedPath == null ? null
                : trimmedPath.replace("$TMP", System.getProperty("java.io.tmpdir"));
    }
}
