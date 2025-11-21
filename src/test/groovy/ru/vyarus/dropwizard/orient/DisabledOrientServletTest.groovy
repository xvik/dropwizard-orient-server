package ru.vyarus.dropwizard.orient


import io.dropwizard.testing.junit5.DropwizardAppExtension
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport
import org.junit.jupiter.api.extension.ExtendWith
import ru.vyarus.dropwizard.orient.support.TestApplication
import ru.vyarus.dropwizard.orient.support.TestConfiguration

/**
 * @author Vyacheslav Rusakov 
 * @since 29.08.2015
 */
@ExtendWith(DropwizardExtensionsSupport)
class DisabledOrientServletTest extends AbstractTest {
    private static final DropwizardAppExtension<TestConfiguration> EXT = new DropwizardAppExtension<>(
            TestApplication.class,
            "src/test/resources/ru/vyarus/dropwizard/orient/conf/noOrientServlet.yml"
    )

    def "Check servlet is not deployed"() {

        when: "accessing orient servlet"
        new URL("http://localhost:8081/orient/").getText()
        then: "not available"
        thrown(FileNotFoundException)
    }
}