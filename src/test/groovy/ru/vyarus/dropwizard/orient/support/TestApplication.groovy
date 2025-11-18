package ru.vyarus.dropwizard.orient.support

import io.dropwizard.core.Application
import io.dropwizard.core.setup.Bootstrap
import io.dropwizard.core.setup.Environment
import ru.vyarus.dropwizard.orient.OrientServerBundle
import ru.vyarus.dropwizard.orient.configuration.OrientServerConfiguration

import java.util.function.Function

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
        bootstrap.addBundle(
                new OrientServerBundle<TestConfiguration>({
                    it.getOrientServerConfiguration()
                } as Function<TestConfiguration, OrientServerConfiguration>))
        bootstrap.addCommand(new DummyCommand())
    }

    @Override
    void run(TestConfiguration configuration, Environment environment) throws Exception {
        environment.jersey().register(EmptyRestService.class)
    }
}
