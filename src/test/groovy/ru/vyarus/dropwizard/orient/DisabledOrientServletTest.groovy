package ru.vyarus.dropwizard.orient

import io.dropwizard.testing.junit.DropwizardAppRule
import org.junit.Rule
import ru.vyarus.dropwizard.orient.support.TestApplication
import ru.vyarus.dropwizard.orient.support.TestConfiguration

/**
 * @author Vyacheslav Rusakov 
 * @since 29.08.2015
 */
class DisabledOrientServletTest extends AbstractTest {

    @Rule
    DropwizardAppRule<TestConfiguration> RULE =
            new DropwizardAppRule<TestConfiguration>(TestApplication.class, 'src/test/resources/ru/vyarus/dropwizard/orient/noOrientServlet.yml');

    def "Check servlet is not deployed"() {

        when: "accessing orient servlet"
        new URL("http://localhost:8081/orient/").getText()
        then: "not available"
        thrown(FileNotFoundException)
    }
}