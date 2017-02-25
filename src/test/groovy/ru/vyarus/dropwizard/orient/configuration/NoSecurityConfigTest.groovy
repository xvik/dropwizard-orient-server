package ru.vyarus.dropwizard.orient.configuration

import com.orientechnologies.orient.object.db.OObjectDatabaseTx
import io.dropwizard.testing.junit.DropwizardAppRule
import org.junit.Rule
import ru.vyarus.dropwizard.orient.AbstractTest
import ru.vyarus.dropwizard.orient.support.TestApplication
import ru.vyarus.dropwizard.orient.support.TestConfiguration

/**
 * @author Vyacheslav Rusakov
 * @since 25.02.2017
 */
class NoSecurityConfigTest extends AbstractTest {

    @Rule
    DropwizardAppRule<TestConfiguration> RULE =
            new DropwizardAppRule<TestConfiguration>(TestApplication.class, 'src/test/resources/ru/vyarus/dropwizard/orient/conf/noSecurity.yml');

    def "Check server starts correctly"() {

        when: "accessing started orient server"
        createRemoteDb('nosecurity')
        OObjectDatabaseTx db = new OObjectDatabaseTx('remote:localhost/nosecurity');
        db.open('admin', 'admin');
        db.close()
        then: "all good"
        true

        when: "accessing orient studio"
        def data = new URL("http://localhost:2480/studio/").getText()
        then: "all good"
        data != null
    }

}