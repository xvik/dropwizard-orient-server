package ru.vyarus.dropwizard.orient

import com.orientechnologies.orient.core.metadata.schema.OClass
import com.orientechnologies.orient.core.metadata.schema.OType
import com.orientechnologies.orient.object.db.OObjectDatabaseTx
import io.dropwizard.testing.junit.DropwizardAppRule
import org.junit.Rule
import ru.vyarus.dropwizard.orient.support.TestApplication
import ru.vyarus.dropwizard.orient.support.TestConfiguration

/**
 * @author Vyacheslav Rusakov 
 * @since 19.03.2015
 */
class LuceneTest extends AbstractTest {

    @Rule
    DropwizardAppRule<TestConfiguration> RULE =
            new DropwizardAppRule<TestConfiguration>(TestApplication.class, 'src/test/resources/ru/vyarus/dropwizard/orient/yamlConfig.yml');

    def "Check graph server starts correctly"() {

        when: "accessing started orient server"
        createRemoteDb('test')
        OObjectDatabaseTx db = new OObjectDatabaseTx('remote:localhost/test');
        db.open('admin', 'admin');
        db.close()
        then: "all good"
        true

        when: "check lucene index creation"
        db.open('admin', 'admin');
        OClass cls = db.getMetadata().getSchema().createClass("test")
        cls.createProperty("text", OType.STRING)
        cls.createIndex("Test.text", "FULLTEXT", null, null, "LUCENE", ["text"] as String[])
        db.close()
        then: "index created"
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
        data = new URL("http://localhost:8081/orient").getText()
        then: "all good"
        data != null
    }
}