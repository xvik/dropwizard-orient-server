package ru.vyarus.dropwizard.orient.internal;

import javax.validation.Path;
import javax.validation.TraversableResolver;
import java.lang.annotation.ElementType;

/**
 * Dummy implementation for {@code TraversableResolver}.
 * Required to substitute jpa implementation, activated by persistence jar
 * (required dependency of orient object database).
 *
 * @author Vyacheslav Rusakov
 * @see ru.vyarus.dropwizard.orient.OrientServerBundle for details
 * @since 17.08.2014
 */
public class DummyTraversableResolver implements TraversableResolver {
    @Override
    public boolean isReachable(
            final Object traversableObject,
            final Path.Node traversableProperty,
            final Class<?> rootBeanType,
            final Path pathToTraversableObject,
            final ElementType elementType) {
        return true;
    }

    @Override
    public boolean isCascadable(
            final Object traversableObject,
            final Path.Node traversableProperty,
            final Class<?> rootBeanType,
            final Path pathToTraversableObject,
            final ElementType elementType) {
        return true;
    }
}
