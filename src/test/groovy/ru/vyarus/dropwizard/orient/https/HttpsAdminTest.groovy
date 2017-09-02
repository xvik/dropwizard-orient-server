package ru.vyarus.dropwizard.orient.https

import io.dropwizard.testing.junit.DropwizardAppRule
import org.junit.Rule
import ru.vyarus.dropwizard.orient.support.TestApplication
import ru.vyarus.dropwizard.orient.support.TestConfiguration

/**
 * @author Vyacheslav Rusakov
 * @since 20.08.2017
 */
class HttpsAdminTest extends AbstractHttpsTest {

    @Rule
    DropwizardAppRule<TestConfiguration> RULE =
            new DropwizardAppRule<TestConfiguration>(TestApplication.class, 'src/test/resources/ru/vyarus/dropwizard/orient/https/httpsAdmin.yml');

    def "Check orient servlet correct with https admin"() {

        when: "accessing orient servlet"
        def data = new URL("https://localhost:8444/orient/").getText()
        then: "all good"
        data != null
        data.contains('<li>Binary ports: 2424</li>')
        data.contains('<li>Http ports: 2480</li>')

        when: "accessing studio through orient servlet"
        data = getGzip("https://localhost:8444/orient/studio")
        then: "all good"
        data.contains('OrientDB LTD')

        then: "redirect to http studio url"
        checkRedirect('https://localhost:8444/orient/studio', 'http://')
    }
}
