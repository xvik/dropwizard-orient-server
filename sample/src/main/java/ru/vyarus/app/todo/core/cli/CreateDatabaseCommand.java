package ru.vyarus.app.todo.core.cli;

import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.db.OrientDB;
import com.orientechnologies.orient.core.db.OrientDBConfig;
import io.dropwizard.core.cli.ConfiguredCommand;
import io.dropwizard.core.setup.Bootstrap;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.argparse4j.inf.Namespace;
import ru.vyarus.app.todo.TodoConfig;
import ru.vyarus.guice.persist.orient.db.util.DBUriUtils;

/**
 * @author Vyacheslav Rusakov
 * @since 25.11.2025
 */
@Slf4j
public class CreateDatabaseCommand extends ConfiguredCommand<TodoConfig> {

    public CreateDatabaseCommand() {
        super("db-create", "Create database");
    }

    @Override
    protected void run(final Bootstrap<TodoConfig> bootstrap,
                       final Namespace namespace,
                       final TodoConfig todoConfig) throws Exception {
        final TodoConfig.DbConfiguration config = todoConfig.getDb();
        final String dbName = DBUriUtils.parseUri(config.getUri())[1];
        // need to use databases folder in order to create db
        final String dbPath;

        // server not started so create plocal database - server will manage it after start
        if (config.getUri().startsWith("remote")) {
            dbPath = ("plocal:" + todoConfig.getOrientServer().getFilesPath() + "/databases").replace("//", "/");
        } else {
            // plocal path already configured
            dbPath  = config.getUri().replace('/' + dbName, "");
        }

        try (OrientDB orientDB = new OrientDB(dbPath, OrientDBConfig.defaultConfig())) {
            if (!orientDB.exists(dbName)) {
                log.info("Creating default database {} with user {}", dbName, config.getUser());

                orientDB.execute("create database " + dbName + " plocal users ( "
                        + config.getUser() + " identified by '" + config.getPass() + "' role admin)");
                log.info("Database {} created", dbName);
            }

            log.info("Testing connection");
            try (final ODatabaseSession session = orientDB.open(dbName, config.getUser(), config.getPass())) {
            }
        }
    }
}
