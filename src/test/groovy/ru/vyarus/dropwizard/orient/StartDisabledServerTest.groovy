package ru.vyarus.dropwizard.orient

import com.orientechnologies.orient.core.exception.ODatabaseException
import com.orientechnologies.orient.object.db.OObjectDatabaseTx
import io.dropwizard.testing.junit5.DropwizardAppExtension
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport
import org.junit.jupiter.api.extension.ExtendWith
import ru.vyarus.dropwizard.orient.support.TestApplication
import ru.vyarus.dropwizard.orient.support.TestConfiguration

/**
 * @author Vyacheslav Rusakov 
 * @since 24.08.2014
 */
@ExtendWith(DropwizardExtensionsSupport)
class StartDisabledServerTest extends AbstractTest {
    private static final DropwizardAppExtension<TestConfiguration> EXT = new DropwizardAppExtension<>(
            TestApplication.class,
            "src/test/resources/ru/vyarus/dropwizard/orient/conf/disabledServer.yml"
    )

    def "Check orient server not started"() {

        when: "accessing started orient server"
        OObjectDatabaseTx db = new OObjectDatabaseTx('remote:localhost/test');
        db.open('admin', 'admin');
        then: "remote connection failed"
        def ex = thrown(ODatabaseException)
        ex.message.startsWith("Cannot open database 'test'")

        when: "accessing orient studio"
        new URL("http://localhost:2480/studio/").getText()
        then: "studio not available"
        thrown(ConnectException)
    }

}