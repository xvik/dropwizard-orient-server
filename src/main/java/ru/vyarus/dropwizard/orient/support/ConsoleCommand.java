package ru.vyarus.dropwizard.orient.support;

import com.google.common.collect.Lists;
import com.orientechnologies.orient.console.OConsoleDatabaseApp;
import io.dropwizard.Configuration;
import io.dropwizard.cli.ConfiguredCommand;
import io.dropwizard.setup.Bootstrap;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import ru.vyarus.dropwizard.orient.configuration.HasOrientServerConfiguration;
import ru.vyarus.dropwizard.orient.configuration.OrientServerConfiguration;

import java.io.File;
import java.util.List;

/**
 * Command is launched in interactive mode if started without parameters: console config.yml.
 * <p>Also can be used to launch commands directly or external commands file. This time process will
 * exit right after commands execution.</p>
 * <p>To execute sql file: console config.yml commands.sql</p>
 * <p>To execute commands: console config.yml command1;command2</p>
 * <p>NOTE: server will not start when console command called, because dropwizard will not run managed objects this time
 * (only server command triggers managed objects lifecycle). But plocal connections still could be used.
 * Also, if server already started, then you can use remote connections.</p>
 * <a href="http://www.orientechnologies.com/docs/last/orientdb.wiki/Console-Commands.html">
 * See orient console documentation</a>
 *
 * @param <T> configuration type
 * @see com.orientechnologies.orient.console.OConsoleDatabaseApp
 */
@SuppressWarnings("PMD.SystemPrintln")
public class ConsoleCommand<T extends Configuration & HasOrientServerConfiguration> extends ConfiguredCommand<T> {

    public static final String COMMANDS_ARG = "commands";
    private Class<T> configClass;

    /**
     * @param configClass configuration class
     */
    public ConsoleCommand(final Class<T> configClass) {
        this(configClass, "console");
    }

    /**
     * @param configClass configuration class
     * @param commandName command name to override default 'console' name
     */
    public ConsoleCommand(final Class<T> configClass, final String commandName) {
        super(commandName, "Run orient db console");
        this.configClass = configClass;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Class<T> getConfigurationClass() {
        // configuration class is required, because real application configuration class is unreachable
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
        final OrientServerConfiguration conf = configuration.getOrientServerConfiguration();
        final List<String> commands = namespace.get(COMMANDS_ARG);
        printHelp(conf, commands);

        OConsoleDatabaseApp.main(commands.toArray(new String[commands.size()]));
    }

    @SuppressWarnings("PMD.UselessParentheses")
    private void printHelp(final OrientServerConfiguration conf, final List<String> commands) {
        System.out.println("See details of command usage: "
                + "http://www.orientechnologies.com/docs/1.7.8/orientdb.wiki/Console-Commands.html");

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
                    System.out.println(String.format("$ connect remote:localhost/%s admin admin", db));
                }
                System.out.println(String.format("$ connect plocal:%s%s admin admin", dbFolder, db));
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
}
