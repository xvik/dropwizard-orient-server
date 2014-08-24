package ru.vyarus.dropwizard.orient

import com.orientechnologies.common.io.OIOException
import com.orientechnologies.orient.object.db.OObjectDatabaseTx
import io.dropwizard.testing.junit.DropwizardAppRule
import org.junit.Rule
import ru.vyarus.dropwizard.orient.support.TestApplication
import ru.vyarus.dropwizard.orient.support.TestConfiguration
import spock.lang.Specification


/**
 * @author Vyacheslav Rusakov 
 * @since 24.08.2014
 */
class StartDisabledServerTest extends AbstractTest {

    @Rule
    DropwizardAppRule<TestConfiguration> RULE =
            new DropwizardAppRule<TestConfiguration>(TestApplication.class, 'src/test/resources/ru/vyarus/dropwizard/orient/disabledServer.yml');

    def "Check orient server not started"() {

        when: "accessing started orient server"
        OObjectDatabaseTx db = new OObjectDatabaseTx('remote:localhost/test');
        db.open('admin', 'admin');
        then: "remote connection failed"
        thrown(OIOException)

        when: "accessing orient studio"
        new URL("http://localhost:2480/studio/").getText()
        then: "studio not available"
        thrown(ConnectException)
    }

}