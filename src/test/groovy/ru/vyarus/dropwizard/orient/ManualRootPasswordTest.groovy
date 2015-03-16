package ru.vyarus.dropwizard.orient

import com.orientechnologies.orient.object.db.OObjectDatabaseTx
import io.dropwizard.testing.junit.DropwizardAppRule
import org.junit.Rule
import org.junit.contrib.java.lang.system.TextFromStandardInputStream
import ru.vyarus.dropwizard.orient.support.TestApplication
import ru.vyarus.dropwizard.orient.support.TestConfiguration

/**
 * @author Vyacheslav Rusakov 
 * @since 16.03.2015
 */
class ManualRootPasswordTest extends AbstractTest {

    @Rule
    DropwizardAppRule<TestConfiguration> RULE =
            new DropwizardAppRule<TestConfiguration>(TestApplication.class, 'src/test/resources/ru/vyarus/dropwizard/orient/yamlNoRootConfig.yml');

    @Rule
    TextFromStandardInputStream systemInMock = new TextFromStandardInputStream("root");

    def "Check server starts correctly without root pass"() {
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
        data && data.contains('"orient-server":{"healthy":true,"message":"OK"}')
    }
}