package ru.vyarus.dropwizard.orient.configuration.deserializer;

import com.orientechnologies.orient.server.config.OServerNetworkProtocolConfiguration;

/**
 * Adds support for "protocol: implementation" style configuration for network section.
 *
 * @author Vyacheslav Rusakov
 * @since 21.02.2017
 */
public class NetworkProtocolDeserializer extends AbstractPairDeserializer<OServerNetworkProtocolConfiguration> {

    @Override
    protected void configure(final OServerNetworkProtocolConfiguration object, final String key, final String value) {
        object.name = key;
        object.implementation = value;
    }
}
