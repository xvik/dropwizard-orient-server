package ru.vyarus.dropwizard.orient


import io.dropwizard.testing.junit5.DropwizardAppExtension
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport
import org.junit.jupiter.api.extension.ExtendWith
import ru.vyarus.dropwizard.orient.support.TestApplication
import ru.vyarus.dropwizard.orient.support.TestConfiguration

/**
 * @author Vyacheslav Rusakov
 * @since 25.02.2017
 */
@ExtendWith(DropwizardExtensionsSupport)
class Studio404Test extends AbstractTest {
    private static final DropwizardAppExtension<TestConfiguration> EXT = new DropwizardAppExtension<>(
            TestApplication.class,
            "src/test/resources/ru/vyarus/dropwizard/orient/yamlConfig.yml"
    )

    def "Check studio properly respond for 404"() {

        when: "access existing resource"
        def data = new URL("http://localhost:2480/studio/index.html").getText()
        then: "all good"
        data != null

        when: "access not existing resource"
        new URL("http://localhost:2480/studio/bad.html").getText()
        then: "error"
        thrown(FileNotFoundException)
    }
}
