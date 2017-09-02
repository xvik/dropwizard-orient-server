package ru.vyarus.dropwizard.orient.https.auto

import io.dropwizard.testing.junit.DropwizardAppRule
import org.junit.Rule
import ru.vyarus.dropwizard.orient.https.AbstractHttpsTest
import ru.vyarus.dropwizard.orient.support.TestApplication
import ru.vyarus.dropwizard.orient.support.TestConfiguration

/**
 * @author Vyacheslav Rusakov
 * @since 03.09.2017
 */
class AutoHttpsSimpleServerTest extends AbstractHttpsTest {
    @Rule
    DropwizardAppRule<TestConfiguration> RULE =
            new DropwizardAppRule<TestConfiguration>(TestApplication.class, 'src/test/resources/ru/vyarus/dropwizard/orient/https/auto/autoHttpsSimpleServer.yml');

    def "Check ssl auto configured from dropwizard simple server connector"() {

        when: "accessing orient servlet"
        def data = new URL("https://localhost:8443/admin/orient/").getText()
        then: "all good"
        data != null
        data.contains('<li>Binary ports: 2434 (ssl)</li>')
        data.contains('<li>Http ports: 2480 (ssl)</li>')

        when: "accessing studio through orient servlet"
        data = getGzip('https://localhost:8443/admin/orient/studio')
        then: "all good"
        data.contains('OrientDB LTD')

        then: "redirect to https studio url"
        checkRedirect('https://localhost:8443/admin/orient/studio', 'https://localhost:2480')
    }
}
