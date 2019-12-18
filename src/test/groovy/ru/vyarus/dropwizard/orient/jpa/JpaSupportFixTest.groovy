package ru.vyarus.dropwizard.orient.jpa


import io.dropwizard.testing.junit.DropwizardAppRule
import org.hibernate.validator.internal.engine.ValidatorImpl
import org.hibernate.validator.internal.engine.resolver.DefaultTraversableResolver
import org.junit.Rule
import ru.vyarus.dropwizard.orient.AbstractTest
import ru.vyarus.dropwizard.orient.support.TestApplication
import ru.vyarus.dropwizard.orient.support.TestConfiguration

/**
 * @author Vyacheslav Rusakov
 * @since 18.12.2019
 */
class JpaSupportFixTest extends AbstractTest {

    @Rule
    DropwizardAppRule<TestConfiguration> RULE =
            new DropwizardAppRule<TestConfiguration>(TestApplication.class, 'src/test/resources/ru/vyarus/dropwizard/orient/yamlConfig.yml');

    def "Check jpa support fixed"() {

        expect: "traversable resolver substituted"
        ((RULE.getEnvironment().getValidator() as ValidatorImpl).traversableResolver as DefaultTraversableResolver)
                .jpaTraversableResolver == null
    }
}
