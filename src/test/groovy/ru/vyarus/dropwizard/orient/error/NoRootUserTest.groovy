package ru.vyarus.dropwizard.orient.error

import ru.vyarus.dropwizard.orient.AbstractTest
import ru.vyarus.dropwizard.orient.support.TestApplication

/**
 * @author Vyacheslav Rusakov 
 * @since 17.03.2015
 */
class NoRootUserTest extends AbstractTest {

    def "Check config without root user"() {

        when: "config without root user"
        TestApplication.main('server', 'src/test/resources/ru/vyarus/dropwizard/orient/bad/badUsersConfig.yml');
        then: "error"
        thrown(IllegalStateException)
    }
}