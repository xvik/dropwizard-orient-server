package ru.vyarus.dropwizard.orient.error


import org.junit.jupiter.api.extension.ExtendWith
import ru.vyarus.dropwizard.orient.AbstractTest
import ru.vyarus.dropwizard.orient.support.TestApplication
import uk.org.webcompere.systemstubs.jupiter.SystemStub
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension
import uk.org.webcompere.systemstubs.security.SystemExit

/**
 * @author Vyacheslav Rusakov 
 * @since 17.03.2015
 */
@ExtendWith(SystemStubsExtension)
class NoRootUserTest extends AbstractTest {

    @SystemStub
    SystemExit exit = new SystemExit()

    def "Check config without root user"() {

        when: "config without root user"
        exit.execute {
            TestApplication.main('server', 'src/test/resources/ru/vyarus/dropwizard/orient/bad/badUsersConfig.yml');
        }
        then: "error"
        exit.getExitCode() == 1
    }
}