package ru.vyarus.dropwizard.orient.configuration.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;

/**
 * Simplifies configuration deserialization for key - value classes. Normally such configuration must be written
 * in two lines: key and value, but this handler allows one line mapping: key: value.
 * <p>
 * Implementation must simply implement key and value binding to provided instance.
 *
 * @param <T> deserializable type (key-value like properties)
 * @author Vyacheslav Rusakov
 * @since 21.02.2017
 */
public abstract class AbstractPairDeserializer<T> extends DeserializationProblemHandler {

    private final Class<T> type;

    @SuppressWarnings("unchecked")
    protected AbstractPairDeserializer() {
        this.type = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean handleUnknownProperty(final DeserializationContext ctxt, final JsonParser jp,
                                         final JsonDeserializer<?> deserializer, final Object beanOrClass,
                                         final String propertyName) throws IOException {
        if (beanOrClass != null && type.isAssignableFrom(beanOrClass.getClass())) {
            configure((T) beanOrClass, propertyName, jp.getValueAsString());
            return true;
        }
        return false;
    }

    protected abstract void configure(T object, String key, String value);
}
