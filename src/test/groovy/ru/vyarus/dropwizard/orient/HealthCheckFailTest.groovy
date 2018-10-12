package ru.vyarus.dropwizard.orient

import com.orientechnologies.orient.core.Orient
import groovyx.net.http.HttpResponseException
import groovyx.net.http.RESTClient
import io.dropwizard.testing.junit.DropwizardAppRule
import org.junit.Rule
import ru.vyarus.dropwizard.orient.support.TestApplication
import ru.vyarus.dropwizard.orient.support.TestConfiguration

/**
 * @author Vyacheslav Rusakov 
 * @since 05.10.2014
 */
class HealthCheckFailTest extends AbstractTest {

    @Rule
    DropwizardAppRule<TestConfiguration> RULE =
            new DropwizardAppRule<TestConfiguration>(TestApplication.class, 'src/test/resources/ru/vyarus/dropwizard/orient/yamlConfig.yml');

    def "Check health check failure detection"() {

        when: "accidentally shutdown orient server"
        Orient.instance().shutdown()
        new RESTClient('http://localhost:8081/healthcheck').get([:])
        then: "health check fails"
        def ex = thrown(HttpResponseException)
        ex.response.status == 500
        ex.response.data['orient-server']['message'] == 'Database not started'

        cleanup:
        Orient.instance().startup()
    }

    def "Check health check storages failure detection"() {

        when: "accidentally shutdown storages orient server"
        // need to close only engines, but not completely shutdown
        Orient.instance().factories.each { it.close() }
        new RESTClient('http://localhost:8081/healthcheck').get([:])
        then: "health check fails"
        def ex = thrown(HttpResponseException)
        ex.response.status == 500
        ex.response.data['orient-server']['message'] == 'No registered storages'

        cleanup:
        Orient.instance().shutdown()
        Orient.instance().startup()
    }
}