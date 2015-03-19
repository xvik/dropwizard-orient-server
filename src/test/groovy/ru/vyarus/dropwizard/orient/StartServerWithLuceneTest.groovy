package ru.vyarus.dropwizard.orient

import com.orientechnologies.orient.object.db.OObjectDatabaseTx
import com.orientechnologies.orient.server.OServerMain
import io.dropwizard.testing.junit.DropwizardAppRule
import org.junit.Rule
import ru.vyarus.dropwizard.orient.support.TestApplication
import ru.vyarus.dropwizard.orient.support.TestConfiguration

/**
 * @author Vyacheslav Rusakov 
 * @since 19.03.2015
 */
class StartServerWithLuceneTest extends AbstractTest {

    @Rule
    DropwizardAppRule<TestConfiguration> RULE =
            new DropwizardAppRule<TestConfiguration>(TestApplication.class, 'src/test/resources/ru/vyarus/dropwizard/orient/yamlLuceneConfig.yml');

    def "Check graph server starts correctly"() {

        when: "accessing started orient server"
        createRemoteDb('test')
        OObjectDatabaseTx db = new OObjectDatabaseTx('remote:localhost/test');
        db.open('admin', 'admin');
        db.close()
        then: "all good"
        true
        OServerMain.server().getPlugin("lucene-index") != null

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