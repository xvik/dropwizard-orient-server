package ru.vyarus.dropwizard.orient.error

import ru.vyarus.dropwizard.orient.support.TestApplication
import spock.lang.Specification

/**
 * @author Vyacheslav Rusakov
 * @since 25.02.2017
 */
class NoServerUsersTest extends Specification {

    def "Check no users in config"() {

        when: "config without server users"
        TestApplication.main('server', 'src/test/resources/ru/vyarus/dropwizard/orient/bad/noUsers.yml');
        then: "error"
        thrown(IllegalStateException)
    }
}
