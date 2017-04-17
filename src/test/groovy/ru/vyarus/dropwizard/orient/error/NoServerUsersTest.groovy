package ru.vyarus.dropwizard.orient.error

import org.junit.Rule
import org.junit.contrib.java.lang.system.ExpectedSystemExit
import org.junit.contrib.java.lang.system.internal.CheckExitCalled
import ru.vyarus.dropwizard.orient.AbstractTest
import ru.vyarus.dropwizard.orient.support.TestApplication

/**
 * @author Vyacheslav Rusakov
 * @since 25.02.2017
 */
class NoServerUsersTest extends AbstractTest {

    @Rule
    ExpectedSystemExit exit = ExpectedSystemExit.none()

    def "Check no users in config"() {
        exit.expectSystemExitWithStatus(1)

        when: "config without server users"
        TestApplication.main('server', 'src/test/resources/ru/vyarus/dropwizard/orient/bad/noUsers.yml');
        then: "error"
        thrown(CheckExitCalled)
    }
}
