package ru.vyarus.dropwizard.orient.error

import org.junit.Rule
import org.junit.contrib.java.lang.system.ExpectedSystemExit
import org.junit.contrib.java.lang.system.internal.CheckExitCalled
import ru.vyarus.dropwizard.orient.AbstractTest
import ru.vyarus.dropwizard.orient.support.TestApplication

/**
 * @author Vyacheslav Rusakov 
 * @since 17.03.2015
 */
class NoRootUserTest extends AbstractTest {

    @Rule
    ExpectedSystemExit exit = ExpectedSystemExit.none()

    def "Check config without root user"() {
        exit.expectSystemExitWithStatus(1)

        when: "config without root user"
        TestApplication.main('server', 'src/test/resources/ru/vyarus/dropwizard/orient/bad/badUsersConfig.yml');
        then: "error"
        thrown(CheckExitCalled)
    }
}