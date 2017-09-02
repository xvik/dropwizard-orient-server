package ru.vyarus.dropwizard.orient.https.auto

import io.dropwizard.testing.junit.DropwizardAppRule
import org.junit.Rule
import ru.vyarus.dropwizard.orient.https.AbstractHttpsTest
import ru.vyarus.dropwizard.orient.support.TestApplication
import ru.vyarus.dropwizard.orient.support.TestConfiguration

/**
 * @author Vyacheslav Rusakov
 * @since 28.08.2017
 */
class AutoHttpsTest extends AbstractHttpsTest {
    @Rule
    DropwizardAppRule<TestConfiguration> RULE =
            new DropwizardAppRule<TestConfiguration>(TestApplication.class, 'src/test/resources/ru/vyarus/dropwizard/orient/https/auto/autoHttps.yml');

    def "Check ssl auto configured from dropwizard connector"() {

        when: "accessing orient servlet"
        def data = new URL("http://localhost:8081/orient/").getText()
        then: "all good"
        data != null
        data.contains('<li>Binary ports: 2434 (ssl)</li>')
        data.contains('<li>Http ports: 2480 (ssl)</li>')

        when: "accessing studio through orient servlet"
        data = getGzip('http://localhost:8081/orient/studio')
        then: "all good"
        data.contains('OrientDB LTD')

        then: "redirect to https studio url"
        checkRedirect('http://localhost:8081/orient/studio', 'https://')

        when: "trying binary protocol"
        createRemoteDb('sample-db')
        then: "ok"
        true
    }
}
