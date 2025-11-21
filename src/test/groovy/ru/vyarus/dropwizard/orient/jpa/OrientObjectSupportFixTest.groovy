package ru.vyarus.dropwizard.orient.jpa


import io.dropwizard.testing.junit5.DropwizardAppExtension
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport
import org.hibernate.validator.internal.engine.ValidatorImpl
import org.hibernate.validator.internal.engine.resolver.JPATraversableResolver
import org.junit.jupiter.api.extension.ExtendWith
import ru.vyarus.dropwizard.orient.AbstractTest
import ru.vyarus.dropwizard.orient.support.TestApplication
import ru.vyarus.dropwizard.orient.support.TestConfiguration

/**
 * @author Vyacheslav Rusakov
 * @since 18.12.2019
 */
@ExtendWith(DropwizardExtensionsSupport)
class OrientObjectSupportFixTest extends AbstractTest {
    private static final DropwizardAppExtension<TestConfiguration> EXT = new DropwizardAppExtension<>(
            TestApplication.class,
            "src/test/resources/ru/vyarus/dropwizard/orient/yamlConfig.yml"
    )

    def "Check jpa support fixed"() {

        expect: "jpa resolver substituted"
        !(((EXT.getEnvironment().getValidator() as ValidatorImpl).traversableResolver) instanceof JPATraversableResolver)
    }
}
