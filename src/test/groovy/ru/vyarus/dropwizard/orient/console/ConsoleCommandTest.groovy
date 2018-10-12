package ru.vyarus.dropwizard.orient.console

import com.orientechnologies.orient.core.Orient
import org.junit.Rule
import org.junit.contrib.java.lang.system.ExpectedSystemExit
import org.junit.contrib.java.lang.system.TextFromStandardInputStream
import org.junit.contrib.java.lang.system.internal.CheckExitCalled
import ru.vyarus.dropwizard.orient.AbstractTest

/**
 * @author Vyacheslav Rusakov 
 * @since 23.08.2014
 */
class ConsoleCommandTest extends AbstractTest {

    // Console is using system exit, so have to use additional rule to catch this
    @Rule
    ExpectedSystemExit exit = ExpectedSystemExit.none();

    @Rule
    TextFromStandardInputStream systemInMock = TextFromStandardInputStream.emptyStandardInputStream();

    void setup() {
        Orient.instance().startup()
    }
    

    def "Check interactive console"() {
        setup: "create db to check help message"
        createLocalDb('test')
        exit.expectSystemExitWithStatus(0)
        systemInMock.provideLines('exit')
        when: "run interactive console and type exit command"
        command 'console src/test/resources/ru/vyarus/dropwizard/orient/yamlConfig.yml'
        then: "all good"
        thrown(CheckExitCalled)
    }

    def "Check interactive console with default help user"() {
        setup: "create db to check help message"
        createLocalDb('test')
        exit.expectSystemExitWithStatus(0)
        systemInMock.provideLines('exit')
        when: "run interactive console wit config without defined users"
        command 'console src/test/resources/ru/vyarus/dropwizard/orient/conf/xmlConfig.yml'
        then: "all good"
        thrown(CheckExitCalled)
    }

    def "Check sql file execution"() {
        setup:
        exit.expectSystemExitWithStatus(0)
        when: "run console with commands file"
        command ('console src/test/resources/ru/vyarus/dropwizard/orient/yamlConfig.yml '
        +'src/test/resources/ru/vyarus/dropwizard/orient/console/commandsFile.sql')
        then: "all good"
        thrown(CheckExitCalled)
    }

    def "Check console command execution"() {
        setup:
        exit.expectSystemExitWithStatus(0)
        when: "run console with command"
        command 'console src/test/resources/ru/vyarus/dropwizard/orient/yamlConfig.yml help'
        then: "all good"
        thrown(CheckExitCalled)
    }

    def "Check console command execution with external config"() {
        setup:
        exit.expectSystemExitWithStatus(0)
        when: "run console with command"
        command 'console src/test/resources/ru/vyarus/dropwizard/orient/conf/xmlConfig.yml help'
        then: "all good"
        thrown(CheckExitCalled)
    }
}