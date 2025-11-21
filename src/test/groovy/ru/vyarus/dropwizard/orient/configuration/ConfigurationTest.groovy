package ru.vyarus.dropwizard.orient.configuration


import org.junit.jupiter.api.extension.ExtendWith
import ru.vyarus.dropwizard.orient.AbstractTest
import ru.vyarus.dropwizard.orient.support.DummyCommand
import uk.org.webcompere.systemstubs.jupiter.SystemStub
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension
import uk.org.webcompere.systemstubs.security.SystemExit

/**
 *
 * @author Vyacheslav Rusakov
 * @since 16.07.2014
 */
@ExtendWith(SystemStubsExtension)
class ConfigurationTest extends AbstractTest {

    @SystemStub
    SystemExit exit = new SystemExit()

    def "Check single config"() {

        when: "inline configuration"
        command 'dummy src/test/resources/ru/vyarus/dropwizard/orient/yamlConfig.yml'
        def conf = DummyCommand.config.orientServerConfiguration

        then: "configuration parsed"
        conf != null
        conf.config != null
        conf.config.handlers != null
        conf.filesPath == System.getProperty("java.io.tmpdir") + '/db/'
        conf.start
    }

    def "Check external config"() {

        when: "external configuration"
        command 'dummy src/test/resources/ru/vyarus/dropwizard/orient/conf/xmlConfig.yml'
        def conf = DummyCommand.config.orientServerConfiguration

        then: "configuration parsed"
        conf != null
        conf.config != null
        conf.config.handlers != null
        conf.filesPath == System.getProperty('java.io.tmpdir') + '/db/'
        conf.start
    }

    def "Check server disabling in config"() {

        when: "start disabled"
        command 'dummy src/test/resources/ru/vyarus/dropwizard/orient/conf/disabledServer.yml'
        def conf = DummyCommand.config.orientServerConfiguration

        then: "configuration parsed"
        conf != null
        conf.config != null
        conf.config.handlers != null
        conf.filesPath == System.getProperty('java.io.tmpdir') + '/db/'
        !conf.start
    }

    def "Check no server config"() {

        when: "no server config specified in file"
        exit.execute {
            command 'dummy src/test/resources/ru/vyarus/dropwizard/orient/bad/noServerConfig.yml'
        }

        then: "configuration validation failed"
        exit.getExitCode() > 0
    }

    def "Check no server files path in config"() {

        when: "no server files path specified in file"
        exit.execute {
            command 'dummy src/test/resources/ru/vyarus/dropwizard/orient/bad/noPathConfig.yml'
        }

        then: "configuration validation failed"
        exit.getExitCode() > 0
    }
}
