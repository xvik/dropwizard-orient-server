package ru.vyarus.dropwizard.orient

import io.dropwizard.testing.junit.DropwizardAppRule
import org.junit.Rule
import org.junit.contrib.java.lang.system.ExpectedSystemExit
import org.junit.contrib.java.lang.system.TextFromStandardInputStream
import org.junit.contrib.java.lang.system.internal.CheckExitCalled
import ru.vyarus.dropwizard.orient.support.TestApplication
import ru.vyarus.dropwizard.orient.support.TestConfiguration

/**
 * @author Vyacheslav Rusakov 
 * @since 17.03.2015
 */
class NoConfigTest extends AbstractTest {

    @Rule
    DropwizardAppRule<TestConfiguration> RULE =
            new DropwizardAppRule<TestConfiguration>(TestApplication.class, 'src/test/resources/ru/vyarus/dropwizard/orient/noConfig.yml');
    // Console is using system exit, so have to use additional rule to catch this

    @Rule
    ExpectedSystemExit exit = ExpectedSystemExit.none();
    @Rule
    TextFromStandardInputStream systemInMock = TextFromStandardInputStream.emptyStandardInputStream();

    def "Check server starts correctly without config"() {

        when: "accessing orient studio"
        new URL("http://localhost:2480/studio/").getText()
        then: "studio not available"
        thrown(ConnectException)
    }

    def "Check command works without config"() {

        setup: "create db to check help message"
        exit.expectSystemExitWithStatus(0)
        systemInMock.provideText('exit\n')
        when: "run interactive console and type exit command"
        command 'console src/test/resources/ru/vyarus/dropwizard/orient/noConfig.yml'
        then: "all good"
        thrown(CheckExitCalled)
    }
}