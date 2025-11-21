package ru.vyarus.dropwizard.orient.util

import com.orientechnologies.common.io.OFileUtils
import com.orientechnologies.orient.core.Orient
import com.orientechnologies.orient.core.config.OGlobalConfiguration
import com.orientechnologies.orient.server.OServerMain
import org.junit.jupiter.api.extension.AfterAllCallback
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtensionContext

/**
 * @author Vyacheslav Rusakov
 * @since 21.11.2025
 */
class DbFolderExtension implements BeforeAllCallback, AfterAllCallback {

    static String dbFolderPath = System.getProperty("java.io.tmpdir") + "/db/";

    @Override
    void beforeAll(ExtensionContext context) throws Exception {
        OGlobalConfiguration.SCRIPT_POLYGLOT_USE_GRAAL.setValue(false)
        OGlobalConfiguration.CREATE_DEFAULT_USERS.setValue(true)
        delete()
    }

    @Override
    void afterAll(ExtensionContext context) throws Exception {
        if (OServerMain.server() != null) {
            OServerMain.server().shutdown()
        }
        delete()
        // no way to reset shutdown state properly
//        OLogManager.instance.shutdownFlag.set(false)


    }

    private void delete() {
        Orient.instance().shutdown();
        OFileUtils.deleteRecursively(new File(dbFolderPath))
        Orient.instance().startup();
    }
}
