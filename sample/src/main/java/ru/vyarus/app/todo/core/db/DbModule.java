package ru.vyarus.app.todo.core.db;

import com.orientechnologies.orient.core.config.OGlobalConfiguration;
import com.orientechnologies.orient.core.db.OrientDBConfig;
import lombok.extern.slf4j.Slf4j;
import ru.vyarus.app.todo.TodoConfig;
import ru.vyarus.dropwizard.guice.module.support.DropwizardAwareModule;
import ru.vyarus.guice.persist.orient.OrientModule;
import ru.vyarus.guice.persist.orient.RepositoryModule;
import ru.vyarus.guice.persist.orient.db.util.DBUriUtils;
import ru.vyarus.guice.persist.orient.support.AutoScanSchemeModule;

/**
 * @author Vyacheslav Rusakov
 * @since 22.11.2025
 */
@Slf4j
public class DbModule extends DropwizardAwareModule<TodoConfig> {

    @Override
    protected void configure() {
        TodoConfig.DbConfiguration db = configuration().getDb();
        final OrientModule orient = new OrientModule(db.getUri(), db.getUser(), db.getPass());
        // enable default users creation for memory db (for tests)
        // real database users would be created either manually or in DbLifecycle
        if (DBUriUtils.isMemory(db.getUri())) {
            log.info("Default users creation enabled for memory database: {}", db.getUri());
            orient.withConfig(OrientDBConfig.builder()
                    .addConfig(OGlobalConfiguration.CREATE_DEFAULT_USERS, true)
                    .build());
        }
        install(orient);
        install(new AutoScanSchemeModule(appPackage() + ".model"));
        install(new RepositoryModule());
    }
}
