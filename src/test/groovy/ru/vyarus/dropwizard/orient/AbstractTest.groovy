package ru.vyarus.dropwizard.orient

import com.orientechnologies.orient.client.remote.OServerAdmin
import com.orientechnologies.orient.core.Orient
import com.orientechnologies.orient.object.db.OObjectDatabaseTx
import ru.vyarus.dropwizard.orient.support.TestApplication;
import spock.lang.Specification

/**
 * Base class for tests.
 *
 * @author Vyacheslav Rusakov
 * @since 16.07.2014
 */
abstract class AbstractTest extends Specification {

    String dbFolderPath = System.getProperty("java.io.tmpdir") + '/db/';

    void setup() {
        new File(dbFolderPath).deleteDir()
        // reset engines state after console exit
        Orient.instance().startup()
    }

    void cleanup() {
        new File(dbFolderPath).deleteDir()
    }

    def command(String attrs) {
        new TestApplication().run(attrs.split(' '))
    }

    def createRemoteDb(String name){
        OServerAdmin admin = new OServerAdmin('remote:localhost/'+name).connect('root', 'root')
        admin.createDatabase('document', 'plocal')
    }

    def createLocalDb(String name) {
        OObjectDatabaseTx db = new OObjectDatabaseTx("plocal:${dbFolderPath}databases/$name")
        if (!db.exists()) db.create()
    }
}
