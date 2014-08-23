package ru.vyarus.dropwizard.orient.support

import io.dropwizard.Application
import io.dropwizard.setup.Bootstrap
import io.dropwizard.setup.Environment
import ru.vyarus.dropwizard.orient.OrientDbBundle

/**
 * @author Vyacheslav Rusakov 
 * @since 18.08.2014
 */
class TestApplication extends Application<TestConfiguration> {

    public static void main(String[] args) {
        new TestApplication().run(args)
    }

    @Override
    void initialize(Bootstrap<TestConfiguration> bootstrap) {
        bootstrap.addBundle(new OrientDbBundle(getConfigurationClass()))
        bootstrap.addCommand(new DummyCommand())
    }

    @Override
    void run(TestConfiguration configuration, Environment environment) throws Exception {
        environment.jersey().register(EmptyRestService.class)
    }
}
