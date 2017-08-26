package ru.vyarus.dropwizard.orient.https

import io.dropwizard.testing.junit.DropwizardAppRule
import org.junit.Rule
import ru.vyarus.dropwizard.orient.support.TestApplication
import ru.vyarus.dropwizard.orient.support.TestConfiguration

/**
 * @author Vyacheslav Rusakov
 * @since 25.08.2017
 */
class HttpsOrientTest extends AbstractHttpsTest {

    @Rule
    DropwizardAppRule<TestConfiguration> RULE =
            new DropwizardAppRule<TestConfiguration>(TestApplication.class, 'src/test/resources/ru/vyarus/dropwizard/orient/https/httpsOrient.yml');

    def "Check orient servlet correctly detect https"() {

        when: "accessing orient servlet"
        def data = new URL("http://localhost:8081/orient/").getText()
        then: "all good"
        data != null
        data.contains('<li>Binary port: 2424</li>')
        data.contains('<li>Http port: 2480 (https enabled)</li>')

        when: "accessing studio through orient servlet"
        data = new URL("http://localhost:8081/orient/studio").getText()
        then: "all good"
        data != null

        then: "redirect to https studio url"
        checkRedirect('http://localhost:8081/orient/studio', 'https://')
    }
}
