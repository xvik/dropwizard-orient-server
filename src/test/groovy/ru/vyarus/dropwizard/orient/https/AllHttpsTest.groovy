package ru.vyarus.dropwizard.orient.https


import io.dropwizard.testing.junit5.DropwizardAppExtension
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport
import org.junit.jupiter.api.extension.ExtendWith
import ru.vyarus.dropwizard.orient.support.TestApplication
import ru.vyarus.dropwizard.orient.support.TestConfiguration

/**
 * @author Vyacheslav Rusakov
 * @since 27.08.2017
 */
@ExtendWith(DropwizardExtensionsSupport)
class AllHttpsTest extends AbstractHttpsTest {
    private static final DropwizardAppExtension<TestConfiguration> EXT = new DropwizardAppExtension<>(
            TestApplication.class,
            "src/test/resources/ru/vyarus/dropwizard/orient/https/allHttps.yml"
    )

    def "Check orient servlet correct with https admin"() {

        when: "accessing orient servlet"
        def data = new URL("https://localhost:8444/orient/").getText()
        then: "all good"
        data != null
        data.contains('<li>Binary ports: 2424 (ssl)</li>')
        data.contains('<li>Http ports: 2480 (ssl)</li>')

        when: "accessing studio through orient servlet"
        data = getGzip("https://localhost:8444/orient/studio")
        then: "all good"
        data.contains('OrientDB LTD')

        then: "redirect to https studio url"
        checkRedirect('https://localhost:8444/orient/studio', 'https://')
    }
}
