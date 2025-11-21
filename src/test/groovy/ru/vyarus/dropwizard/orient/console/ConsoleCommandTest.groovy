package ru.vyarus.dropwizard.orient.console

import com.orientechnologies.orient.core.Orient
import org.junit.jupiter.api.extension.ExtendWith
import ru.vyarus.dropwizard.orient.AbstractTest
import uk.org.webcompere.systemstubs.jupiter.SystemStub
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension
import uk.org.webcompere.systemstubs.security.SystemExit
import uk.org.webcompere.systemstubs.stream.SystemIn

/**
 * @author Vyacheslav Rusakov 
 * @since 23.08.2014
 */
@ExtendWith(SystemStubsExtension)
class ConsoleCommandTest extends AbstractTest {

    @SystemStub
    SystemExit exit = new SystemExit()

    void setup() {
        Orient.instance().startup()
    }


    def "Check interactive console"() {
        setup: "create db to check help message"
        createLocalDb('test')

        when: "run interactive console and type exit command"
        exit.execute {
            new SystemIn("exit").execute {
                command 'console src/test/resources/ru/vyarus/dropwizard/orient/yamlConfig.yml'
            }
        }

        then: "all good"
        exit.getExitCode() == 0
    }

    def "Check interactive console with default help user"() {
        setup: "create db to check help message"
        createLocalDb('test')

        when: "run interactive console wit config without defined users"
        exit.execute {
            new SystemIn("exit").execute {
                command 'console src/test/resources/ru/vyarus/dropwizard/orient/conf/xmlConfig.yml'
            }
        }

        then: "all good"
        exit.getExitCode() == 0
    }

    def "Check sql file execution"() {
        when: "run console with commands file"
        exit.execute {
            command('console src/test/resources/ru/vyarus/dropwizard/orient/yamlConfig.yml '
                    + 'src/test/resources/ru/vyarus/dropwizard/orient/console/commandsFile.sql')
        }

        then: "all good"
        exit.getExitCode() == 0
    }

    def "Check console command execution"() {
        when: "run console with command"
        exit.execute {
            command 'console src/test/resources/ru/vyarus/dropwizard/orient/yamlConfig.yml help'
        }

        then: "all good"
        exit.getExitCode() == 0
    }

    def "Check console command execution with external config"() {
        when: "run console with command"
        exit.execute {
            command 'console src/test/resources/ru/vyarus/dropwizard/orient/conf/xmlConfig.yml help'
        }

        then: "all good"
        exit.getExitCode() == 0
    }
}