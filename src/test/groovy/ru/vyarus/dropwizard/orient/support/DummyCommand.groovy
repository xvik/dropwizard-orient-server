package ru.vyarus.dropwizard.orient.support

import io.dropwizard.cli.ConfiguredCommand
import io.dropwizard.setup.Bootstrap
import net.sourceforge.argparse4j.inf.Namespace

/**
 * Dummy command to resolve config
 * @author Vyacheslav Rusakov 
 * @since 23.08.2014
 */
class DummyCommand extends ConfiguredCommand<TestConfiguration>{

    static TestConfiguration config;

    DummyCommand() {
        super("dummy", "")
    }

    @Override
    protected void run(Bootstrap<TestConfiguration> bootstrap, Namespace namespace, TestConfiguration configuration) throws Exception {
        config = configuration;
    }
}
