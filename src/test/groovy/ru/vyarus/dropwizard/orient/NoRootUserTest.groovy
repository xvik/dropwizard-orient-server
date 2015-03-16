package ru.vyarus.dropwizard.orient

import ru.vyarus.dropwizard.orient.support.TestApplication
import spock.lang.Specification

/**
 * @author Vyacheslav Rusakov 
 * @since 17.03.2015
 */
class NoRootUserTest extends Specification {

    def "Check config without root user"() {

        when: "config without root user"
        TestApplication.main('server', 'src/test/resources/ru/vyarus/dropwizard/orient/bad/badUsersConfig.yml');
        then: "error"
        thrown(IllegalStateException)
    }
}