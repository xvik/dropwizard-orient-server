package ru.vyarus.dropwizard.orient.configuration;

/**
 * Marker interface for configuration.
 * Implement it to define access for orient server configuration from application configuration.
 */
public interface HasOrientServerConfiguration {

    /**
     * @return orient configuration object or null
     */
    OrientServerConfiguration getOrientServerConfiguration();
}
