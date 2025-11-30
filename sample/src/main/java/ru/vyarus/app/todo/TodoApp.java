package ru.vyarus.app.todo;

import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.core.Application;
import io.dropwizard.core.setup.Bootstrap;
import io.dropwizard.core.setup.Environment;
import ru.vyarus.app.todo.core.cli.CreateDatabaseCommand;
import ru.vyarus.app.todo.core.db.DbModule;
import ru.vyarus.dropwizard.guice.GuiceBundle;
import ru.vyarus.dropwizard.orient.OrientServerBundle;

/**
 * @author Vyacheslav Rusakov
 * @since 11.09.2025
 */
public class TodoApp extends Application<TodoConfig> {

    public static void main(String[] args) throws Exception {
        new TodoApp().run(args);
    }

    @Override
    public void initialize(Bootstrap<TodoConfig> bootstrap) {
        bootstrap.addCommand(new CreateDatabaseCommand());

        bootstrap.addBundle(GuiceBundle.builder()
                .enableAutoConfig()
                .dropwizardBundles(
                        new AssetsBundle("/assets/", "/", "index.html"),
                        new OrientServerBundle<>(TodoConfig::getOrientServer))
                .modules(new DbModule())
                .build());
    }

    @Override
    public void run(TodoConfig configuration, Environment environment) throws Exception {
    }
}
