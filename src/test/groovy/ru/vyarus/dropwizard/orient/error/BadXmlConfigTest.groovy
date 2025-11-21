package ru.vyarus.dropwizard.orient.error


import org.junit.jupiter.api.extension.ExtendWith
import ru.vyarus.dropwizard.orient.AbstractTest
import uk.org.webcompere.systemstubs.jupiter.SystemStub
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension
import uk.org.webcompere.systemstubs.security.SystemExit

/**
 * @author Vyacheslav Rusakov
 * @since 25.02.2017
 */
@ExtendWith(SystemStubsExtension)
class BadXmlConfigTest extends AbstractTest {

    @SystemStub
    SystemExit exit = new SystemExit();

    def "Check bad server xml config"() {

        when: "bad config xml in file"
        exit.execute {
            command 'dummy src/test/resources/ru/vyarus/dropwizard/orient/bad/badXmlConfig.yml'
        }

        then: "configuration validation failed"
        exit.getExitCode() == 1
    }
}
