package ru.vyarus.dropwizard.orient

import io.dropwizard.testing.junit.DropwizardAppRule
import org.junit.Rule
import ru.vyarus.dropwizard.orient.support.TestApplication
import ru.vyarus.dropwizard.orient.support.TestConfiguration

/**
 * @author Vyacheslav Rusakov
 * @since 25.02.2017
 */
class ApiRedirectTest extends AbstractTest {
    @Rule
    DropwizardAppRule<TestConfiguration> RULE =
            new DropwizardAppRule<TestConfiguration>(TestApplication.class, 'src/test/resources/ru/vyarus/dropwizard/orient/yamlConfig.yml');

    def "Check rest api is accessible with prefix"() {

        when: "access rest api"
        def data = new URL("http://localhost:2480/listDatabases").getText()
        then: "all good"
        data == "{\"@type\":\"d\",\"@version\":0,\"databases\":[\"OSystem\"]}"

        when: "access rest api through prefix"
        def apiData = new URL("http://localhost:2480/api/listDatabases").getText()
        then: "all good"
        apiData == data

    }
}
