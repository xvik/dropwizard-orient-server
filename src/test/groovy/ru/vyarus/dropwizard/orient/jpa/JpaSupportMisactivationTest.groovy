package ru.vyarus.dropwizard.orient.jpa

import org.hibernate.validator.internal.engine.resolver.JPATraversableResolver
import org.hibernate.validator.internal.engine.resolver.TraversableResolvers
import ru.vyarus.dropwizard.orient.AbstractTest

/**
 * @author Vyacheslav Rusakov
 * @since 18.12.2019
 */
class JpaSupportMisactivationTest extends AbstractTest {

    def "Check jpa resover enabled by default"() {

        when: "get default resolver"
        def res = TraversableResolvers.getDefault()
        then: "by default jpa resolver activated due to classpath"
        !(res instanceof JPATraversableResolver)
    }
}
