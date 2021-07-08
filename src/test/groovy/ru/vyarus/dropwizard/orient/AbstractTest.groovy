package ru.vyarus.dropwizard.orient

import com.orientechnologies.common.log.OLogManager
import com.orientechnologies.orient.core.config.OGlobalConfiguration
import com.orientechnologies.orient.core.db.ODatabaseType
import com.orientechnologies.orient.core.db.OrientDB
import com.orientechnologies.orient.core.db.OrientDBConfig
import ru.vyarus.dropwizard.orient.support.TestApplication
import spock.lang.Shared
import spock.lang.Specification

/**
 * Base class for tests.
 *
 * @author Vyacheslav Rusakov
 * @since 16.07.2014
 */
abstract class AbstractTest extends Specification {

    @Shared
    String dbFolderPath = System.getProperty("java.io.tmpdir") + '/db/';

    void setupSpec() {
        OGlobalConfiguration.SCRIPT_POLYGLOT_USE_GRAAL.setValue(false)
        OGlobalConfiguration.CREATE_DEFAULT_USERS.setValue(true)
        new File(dbFolderPath).deleteDir()
    }

    void cleanupSpec() {
        // no way to reset shutdown state properly
        OLogManager.instance.shutdownFlag.set(false)
        new File(dbFolderPath).deleteDir()
    }

    def command(String attrs) {
        new TestApplication().run(attrs.split(' '))
    }

    def createRemoteDb(String name) {
        OrientDB orientDb = new OrientDB(
                "remote:localhost", "root", "root", OrientDBConfig.defaultConfig());
        orientDb.createIfNotExists(name, ODatabaseType.PLOCAL)
        orientDb.close()
    }

    def createLocalDb(String name) {
        OrientDB orientDb = new OrientDB(
                "embedded:${dbFolderPath}/databases/", OrientDBConfig.defaultConfig());
        orientDb.createIfNotExists(name, ODatabaseType.PLOCAL)
        orientDb.close()
    }
}
