package ru.vyarus.dropwizard.orient.configuration;

/**
 * Marker interface for configuration.
 * Implement it to define access for orient configuration from application configuration.
 */
public interface HasOrientConfiguration {

    /**
     * @return orient configuration object or null
     */
    OrientConfiguration getOrientConfiguration();
}
