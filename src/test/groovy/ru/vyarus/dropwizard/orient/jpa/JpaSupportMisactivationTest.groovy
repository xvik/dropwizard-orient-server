package ru.vyarus.dropwizard.orient.jpa


import org.hibernate.validator.internal.engine.resolver.DefaultTraversableResolver
import ru.vyarus.dropwizard.orient.AbstractTest

/**
 * @author Vyacheslav Rusakov
 * @since 18.12.2019
 */
class JpaSupportMisactivationTest extends AbstractTest {

    def "Check jpa resover enabled by default"() {

        when: "default resolver includes implicit jpa detection"
        def res = new DefaultTraversableResolver()
        then: "jpa resolver instantiated internally and will be used for checks"
        res.jpaTraversableResolver != null
    }
}
