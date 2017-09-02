package ru.vyarus.dropwizard.orient.https

import io.dropwizard.testing.junit.DropwizardAppRule
import org.junit.Rule
import ru.vyarus.dropwizard.orient.support.TestApplication
import ru.vyarus.dropwizard.orient.support.TestConfiguration

/**
 * @author Vyacheslav Rusakov
 * @since 02.09.2017
 */
class MultiplePortsTest extends AbstractHttpsTest {

    @Rule
    DropwizardAppRule<TestConfiguration> RULE =
            new DropwizardAppRule<TestConfiguration>(TestApplication.class, 'src/test/resources/ru/vyarus/dropwizard/orient/https/multiplePorts.yml');

    def "Check dual orient config (http and https)"() {

        when: "accessing orient servlet"
        def data = new URL("http://localhost:8081/orient/").getText()
        then: "all good"
        data != null
        data.contains('<li>Binary ports: 2424, 2434 (ssl)</li>')
        data.contains('<li>Http ports: 2480, 2491 (ssl)</li>')

        when: "accessing studio through orient servlet"
        data = getGzip("http://localhost:8081/orient/studio")
        then: "all good"
        data.contains('OrientDB LTD')

        then: "redirect to https studio url"
        checkRedirect('http://localhost:8081/orient/studio', 'https://localhost:2491')
    }
}
