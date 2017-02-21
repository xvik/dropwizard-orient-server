package ru.vyarus.dropwizard.orient.configuration.deserializer;

import com.orientechnologies.orient.server.config.OServerParameterConfiguration;

/**
 * Adds support for "name: value" style configuration for parameters section of commands and handlers.
 *
 * @author Vyacheslav Rusakov
 * @since 21.02.2017
 */
public class ParameterDeserializer extends AbstractPairDeserializer<OServerParameterConfiguration> {

    @Override
    protected void configure(final OServerParameterConfiguration object, final String key, final String value) {
        object.name = key;
        object.value = value;
    }
}
