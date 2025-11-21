package ru.vyarus.dropwizard.orient.error


import io.dropwizard.testing.junit5.DropwizardAppExtension
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport
import org.junit.jupiter.api.extension.ExtendWith
import ru.vyarus.dropwizard.orient.AbstractTest
import ru.vyarus.dropwizard.orient.support.TestApplication
import ru.vyarus.dropwizard.orient.support.TestConfiguration
import uk.org.webcompere.systemstubs.jupiter.SystemStub
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension
import uk.org.webcompere.systemstubs.security.SystemExit
import uk.org.webcompere.systemstubs.stream.SystemIn

/**
 * @author Vyacheslav Rusakov 
 * @since 17.03.2015
 */

@ExtendWith([DropwizardExtensionsSupport, SystemStubsExtension])
class NoConfigTest extends AbstractTest {
    private static final DropwizardAppExtension<TestConfiguration> EXT = new DropwizardAppExtension<>(
            TestApplication.class,
            "src/test/resources/ru/vyarus/dropwizard/orient/conf/noConfig.yml"
    )

    // Console is using system exit, so have to use additional rule to catch this
    @SystemStub
    SystemExit exit = new SystemExit()

    def "Check server starts correctly without config"() {

        when: "accessing orient studio"
        new URL("http://localhost:2480/studio/").getText()
        then: "studio not available"
        thrown(IOException)
    }

    def "Check command works without config"() {

        when: "run interactive console and type exit command"
        exit.execute {
            new SystemIn('exit').execute {
                command 'console src/test/resources/ru/vyarus/dropwizard/orient/conf/noConfig.yml'
            }
        }

        then: "all good"
        exit.getExitCode() == 0
    }
}