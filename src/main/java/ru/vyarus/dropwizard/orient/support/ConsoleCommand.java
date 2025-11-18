package ru.vyarus.dropwizard.orient.support;

import com.google.common.collect.Lists;
import com.orientechnologies.orient.console.OConsoleDatabaseApp;
import io.dropwizard.Configuration;
import io.dropwizard.cli.ConfiguredCommand;
import io.dropwizard.setup.Bootstrap;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import ru.vyarus.dropwizard.orient.configuration.OrientServerConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.function.Function;

/**
 * Command is launched in interactive mode if started without parameters: console config.yml.
 * <p>
 * Also can be used to launch sql commands directly or external scripts file. This time, process will
 * exit right after commands execution.
 * <p>
 * To execute sql file: {@code console config.yml commands.sql}
 * <p>
 * To execute commands: {@code console config.yml command1;command2}*
 * <p>
 * NOTE: server will not start when console command called, because dropwizard will not run managed objects this time
 * (only server command triggers managed objects lifecycle). But plocal connections still could be used.
 * Also, if server already started, then you can use remote connections.
 * <p>
 * <a href="https://orientdb.org/docs/3.0.x/console/Console-Commands.html">
 * See orient console documentation</a>
 *
 * @param <T> configuration type
 * @see com.orientechnologies.orient.console.OConsoleDatabaseApp
 */
@SuppressWarnings("PMD.SystemPrintln")
public class ConsoleCommand<T extends Configuration> extends ConfiguredCommand<T> {

    public static final String COMMANDS_ARG = "commands";
    private final Class<T> configClass;
    private final Function<T, OrientServerConfiguration> configurationProvider;

    public ConsoleCommand(final Class<T> configClass,
                          final Function<T, OrientServerConfiguration> configurationProvider) {
        super("console", "Run orient db console");
        this.configClass = configClass;
        this.configurationProvider = configurationProvider;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Class<T> getConfigurationClass() {
        // configuration class is required to properly map configuration
        return configClass;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void configure(final Subparser subparser) {
        super.configure(subparser);
        subparser.addArgument(COMMANDS_ARG).nargs("*").help("orient console commands or commands file");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void run(final Bootstrap<T> bootstrap, final Namespace namespace,
                       final T configuration) throws Exception {
        final OrientServerConfiguration conf = configurationProvider.apply(configuration);
        final List<String> commands = namespace.get(COMMANDS_ARG);
        printHelp(conf, commands);

        OConsoleDatabaseApp.main(commands.toArray(new String[0]));
    }

    private void printHelp(final OrientServerConfiguration conf, final List<String> commands) {
        System.out.println("See details of command usage: "
                + "https://orientdb.org/docs/3.0.x/console/Console-Commands.html");

        if (conf == null) {
            return;
        }
        final boolean isInteractiveMode = commands.isEmpty();
        final String dbFolder = (conf.getFilesPath() + "/databases/").replaceAll("//", "/");
        final List<String> availableDatabases = getDatabases(dbFolder);

        // print help message
        if (isInteractiveMode && !availableDatabases.isEmpty()) {
            System.out.println("To connect database use one of the following commands:");
            for (String db : availableDatabases) {
                if (conf.isStart()) {
                    System.out.println(String.format("$ connect remote:localhost/%s root root", db));
                }
                System.out.println(String.format("$ connect plocal:%s root root", cleanupDbPath(dbFolder, db)));
            }
        }
    }

    private List<String> getDatabases(final String dbFolder) {
        final List<String> availableDatabases = Lists.newArrayList();
        final File file = new File(dbFolder);
        if (file.exists() && file.isDirectory()) {
            final File[] files = file.listFiles();
            if (files != null) {
                for (File db : files) {
                    if (db.isDirectory()) {
                        availableDatabases.add(db.getName());
                    }
                }
            }
        }
        return availableDatabases;
    }

    private String cleanupDbPath(final String basedir, final String name) {
        String res = basedir + name;
        try {
            res = new File(res).getCanonicalPath();
        } catch (IOException ignored) {
            // ignore: in worse case badly formatted path will be shown
        }
        return res;
    }
}
