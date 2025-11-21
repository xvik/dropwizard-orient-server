package ru.vyarus.dropwizard.orient.error


import org.junit.jupiter.api.extension.ExtendWith
import ru.vyarus.dropwizard.orient.AbstractTest
import ru.vyarus.dropwizard.orient.support.TestApplication
import uk.org.webcompere.systemstubs.jupiter.SystemStub
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension
import uk.org.webcompere.systemstubs.security.SystemExit

/**
 * @author Vyacheslav Rusakov
 * @since 25.02.2017
 */
@ExtendWith(SystemStubsExtension)
class NoServerUsersTest extends AbstractTest {

    @SystemStub
    SystemExit exit = new SystemExit()

    def "Check no users in config"() {
        when: "config without server users"
        exit.execute {
            TestApplication.main('server', 'src/test/resources/ru/vyarus/dropwizard/orient/bad/noUsers.yml');
        }

        then: "error"
        exit.getExitCode() == 1
    }
}
