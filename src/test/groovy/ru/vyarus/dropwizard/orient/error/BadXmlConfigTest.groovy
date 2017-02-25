package ru.vyarus.dropwizard.orient.error

import org.junit.Rule
import org.junit.contrib.java.lang.system.ExpectedSystemExit
import org.junit.contrib.java.lang.system.internal.CheckExitCalled
import ru.vyarus.dropwizard.orient.AbstractTest

/**
 * @author Vyacheslav Rusakov
 * @since 25.02.2017
 */
class BadXmlConfigTest extends AbstractTest {

    @Rule
    ExpectedSystemExit exit = ExpectedSystemExit.none();

    def "Check bad server xml config"() {

        setup:
        exit.expectSystemExitWithStatus(1)

        when: "bad config xml in file"
        command 'dummy src/test/resources/ru/vyarus/dropwizard/orient/bad/badXmlConfig.yml'

        then: "configuration validation failed"
        thrown(CheckExitCalled)
    }
}
