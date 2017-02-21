package ru.vyarus.dropwizard.orient.configuration.deserializer;

import com.orientechnologies.orient.server.config.OServerEntryConfiguration;

/**
 * Adds support for "name: value" style configuration for properties section.
 *
 * @author Vyacheslav Rusakov
 * @since 21.02.2017
 */
public class EntryDeserializer extends AbstractPairDeserializer<OServerEntryConfiguration> {

    @Override
    protected void configure(final OServerEntryConfiguration object, final String key, final String value) {
        object.name = key;
        object.value = value;
    }
}
