package ru.vyarus.dropwizard.orient

import org.junit.Rule
import org.junit.contrib.java.lang.system.ExpectedSystemExit
import org.junit.contrib.java.lang.system.internal.CheckExitCalled
import ru.vyarus.dropwizard.orient.support.DummyCommand

/**
 *
 * @author Vyacheslav Rusakov
 * @since 16.07.2014
 */
class ConfigurationTest extends AbstractTest {

    @Rule
    ExpectedSystemExit exit = ExpectedSystemExit.none();

    def "Check single config"() {

        when: "inline configuration"
        command 'dummy src/test/resources/ru/vyarus/dropwizard/orient/yamlConfig.yml'
        def conf = DummyCommand.config.orientConfiguration

        then: "configuration parsed"
        conf != null
        conf.config != null
        conf.config.handlers != null
        conf.filesPath == System.getProperty("java.io.tmpdir")+'/db/'
        conf.start
    }

    def "Check external config"() {

        when: "external configuration"
        command 'dummy src/test/resources/ru/vyarus/dropwizard/orient/xmlConfig.yml'
        def conf = DummyCommand.config.orientConfiguration

        then: "configuration parsed"
        conf != null
        conf.config != null
        conf.config.handlers != null
        conf.filesPath == '/tmp/db/'
        conf.start
    }

    def "Check server disabling in config"() {

        when: "start disabled"
        command 'dummy src/test/resources/ru/vyarus/dropwizard/orient/disabledServer.yml'
        def conf = DummyCommand.config.orientConfiguration

        then: "configuration parsed"
        conf != null
        conf.config != null
        conf.config.handlers != null
        conf.filesPath == '/tmp/db/'
        !conf.start
    }

    def "Check no server config"() {

        setup:
        exit.expectSystemExitWithStatus(1)

        when: "no server config specified in file"
        command 'dummy src/test/resources/ru/vyarus/dropwizard/orient/bad/noServerConfig.yml'

        then: "configuration validation failed"
        thrown(CheckExitCalled)
    }

    def "Check no server files path in config"() {

        setup:
        exit.expectSystemExitWithStatus(1)

        when: "no server files path specified in file"
        command 'dummy src/test/resources/ru/vyarus/dropwizard/orient/bad/noPathConfig.yml'

        then: "configuration validation failed"
        thrown(CheckExitCalled)
    }
}
