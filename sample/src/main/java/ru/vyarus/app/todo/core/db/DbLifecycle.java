package ru.vyarus.app.todo.core.db;

import com.google.inject.Inject;
import io.dropwizard.lifecycle.Managed;
import lombok.extern.slf4j.Slf4j;
import ru.vyarus.app.todo.TodoConfig;
import ru.vyarus.dropwizard.guice.module.yaml.bind.Config;
import ru.vyarus.guice.persist.orient.db.DatabaseManager;

/**
 * @author Vyacheslav Rusakov
 * @since 22.11.2025
 */
@Slf4j
public class DbLifecycle implements Managed {

    @Inject
    private DatabaseManager orientService;
    @Inject
    @Config
    private TodoConfig.DbConfiguration config;

    @Override
    public void start() throws Exception {
        orientService.start();
    }

    @Override
    public void stop() throws Exception {
        orientService.stop();
    }
}
