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
class JsonSecurityTest extends AbstractTest {
    @Rule
    DropwizardAppRule<TestConfiguration> RULE =
            new DropwizardAppRule<TestConfiguration>(TestApplication.class, 'src/test/resources/ru/vyarus/dropwizard/orient/conf/jsonSecurity.yml');

    def "Check server starts correctly"() {

        when: "accessing started orient server"
        createRemoteDb('json')
        OObjectDatabaseTx db = new OObjectDatabaseTx('remote:localhost/json');
        db.open('admin', 'admin');
        db.close()
        then: "all good"
        true
    }
}
