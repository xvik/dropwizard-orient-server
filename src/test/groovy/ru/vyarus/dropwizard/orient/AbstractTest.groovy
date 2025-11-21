package ru.vyarus.dropwizard.orient


import com.orientechnologies.orient.core.db.ODatabaseType
import com.orientechnologies.orient.core.db.OrientDB
import com.orientechnologies.orient.core.db.OrientDBConfig
import org.junit.jupiter.api.extension.ExtendWith
import ru.vyarus.dropwizard.orient.support.TestApplication
import ru.vyarus.dropwizard.orient.util.DbFolderExtension
import spock.lang.Specification

/**
 * Base class for tests.
 *
 * @author Vyacheslav Rusakov
 * @since 16.07.2014
 */
@ExtendWith(DbFolderExtension)
abstract class AbstractTest extends Specification {

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
                "embedded:${DbFolderExtension.dbFolderPath}/databases/", OrientDBConfig.defaultConfig());
        orientDb.createIfNotExists(name, ODatabaseType.PLOCAL)
        orientDb.close()
    }
}
