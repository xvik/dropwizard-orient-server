package ru.vyarus.dropwizard.orient.https


import io.dropwizard.testing.junit5.DropwizardAppExtension
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport
import org.junit.jupiter.api.extension.ExtendWith
import ru.vyarus.dropwizard.orient.support.TestApplication
import ru.vyarus.dropwizard.orient.support.TestConfiguration

/**
 * @author Vyacheslav Rusakov
 * @since 25.08.2017
 */
@ExtendWith(DropwizardExtensionsSupport)
class HttpsOrientTest extends AbstractHttpsTest {
    private static final DropwizardAppExtension<TestConfiguration> EXT = new DropwizardAppExtension<>(
            TestApplication.class,
            "src/test/resources/ru/vyarus/dropwizard/orient/https/httpsOrient.yml"
    )

    def "Check orient servlet correctly detect https"() {

        when: "accessing orient servlet"
        def data = new URL("http://localhost:8081/orient/").getText()
        then: "all good"
        data != null
        data.contains('<li>Binary ports: 2424</li>')
        data.contains('<li>Http ports: 2480 (ssl)</li>')

        when: "accessing studio through orient servlet"
        data = getGzip("http://localhost:8081/orient/studio")
        then: "all good"
        data.contains('OrientDB LTD')

        then: "redirect to https studio url"
        checkRedirect('http://localhost:8081/orient/studio', 'https://')
    }
}
