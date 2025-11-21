package ru.vyarus.dropwizard.orient

import com.orientechnologies.orient.core.Orient
import io.dropwizard.testing.junit5.DropwizardAppExtension
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport
import org.junit.jupiter.api.extension.ExtendWith
import ru.vyarus.dropwizard.orient.support.TestApplication
import ru.vyarus.dropwizard.orient.support.TestConfiguration

import javax.ws.rs.core.Response

/**
 * @author Vyacheslav Rusakov 
 * @since 05.10.2014
 */
@ExtendWith(DropwizardExtensionsSupport)
class HealthCheckFailTest extends AbstractTest {

    private static final DropwizardAppExtension<TestConfiguration> EXT = new DropwizardAppExtension<>(
            TestApplication.class,
            "src/test/resources/ru/vyarus/dropwizard/orient/yamlConfig.yml"
    )

    def "Check health check failure detection"() {

        when: "accidentally shutdown orient server"
        Orient.instance().shutdown()
        Response res = EXT.client().target('http://localhost:8081/healthcheck').request().get()

        then: "health check fails"
        res.status == 500
        res.readEntity(String.class).contains('Database not started')

        cleanup:
        Orient.instance().startup()
    }

    def "Check health check storages failure detection"() {

        when: "accidentally shutdown storages orient server"
        // need to close only engines, but not completely shutdown
        Orient.instance().factories.each { it.close() }
        Response res = EXT.client().target('http://localhost:8081/healthcheck').request().get()

        then: "health check fails"
        res.status == 500
        res.readEntity(String.class).contains('No registered storages')

        cleanup:
        Orient.instance().shutdown()
        Orient.instance().startup()
    }
}