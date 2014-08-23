package ru.vyarus.dropwizard.orient

import com.orientechnologies.orient.client.remote.OServerAdmin
import com.orientechnologies.orient.object.db.OObjectDatabaseTx

/**
 * @author Vyacheslav Rusakov 
 * @since 23.08.2014
 */
class StartApplicationTest extends AbstractTest {

    String dbFolderPath = System.getProperty("java.io.tmpdir") + '/db/';

    void setup() {
        new File(dbFolderPath).deleteDir()
    }

    void cleanup() {
        new File(dbFolderPath).deleteDir()
    }

    def "Check server starts correctly"() {

        when: "run server to check orient proper start"
        command 'server src/test/resources/ru/vyarus/dropwizard/orient/yamlConfig.yml'
        then: "all good"
        true

        when: "accessing started orient server"
        OServerAdmin admin = new OServerAdmin('remote:localhost/test').connect('admin', 'admin')
        admin.createDatabase('document', 'local')
        OObjectDatabaseTx db = new OObjectDatabaseTx('remote:localhost/test');
        db.open('admin', 'admin');
        db.close()
        then: "all good"
        true

        when: "accessing orient studio"
        def data = new URL("http://localhost:2480/studio/").getText()
        then: "all good"
        data != null

        // server will be implicitly closed
    }
}