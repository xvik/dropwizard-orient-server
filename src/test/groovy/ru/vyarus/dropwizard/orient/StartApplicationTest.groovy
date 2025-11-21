package ru.vyarus.dropwizard.orient

import com.orientechnologies.orient.object.db.OObjectDatabaseTx
import io.dropwizard.testing.junit5.DropwizardAppExtension
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport
import org.junit.jupiter.api.extension.ExtendWith
import ru.vyarus.dropwizard.orient.support.TestApplication
import ru.vyarus.dropwizard.orient.support.TestConfiguration

/**
 * @author Vyacheslav Rusakov 
 * @since 23.08.2014
 */
@ExtendWith(DropwizardExtensionsSupport)
class StartApplicationTest extends AbstractTest {
    private static final DropwizardAppExtension<TestConfiguration> EXT = new DropwizardAppExtension<>(
            TestApplication.class,
            "src/test/resources/ru/vyarus/dropwizard/orient/yamlConfig.yml"
    )

    def "Check server starts correctly"() {

        when: "accessing started orient server"
        createRemoteDb('test')
        OObjectDatabaseTx db = new OObjectDatabaseTx('remote:localhost/test');
        db.open('admin', 'admin');
        db.close()
        then: "all good"
        true

        when: "accessing orient studio"
        def data = new URL("http://localhost:2480/studio/").getText()
        then: "all good"
        data != null

        when: "accessing health checks"
        data = new URL("http://localhost:8081/healthcheck").getText()
        then: "all good"
        data && data.contains('"orient-server":{"healthy":true,"message":"OK"')

        when: "accessing orient servlet"
        data = new URL("http://localhost:8081/orient/").getText()
        then: "all good"
        data != null

        when: "accessing studio through orient servlet"
        data = new URL("http://localhost:8081/orient/studio").getText()
        then: "all good"
        data != null
    }
}