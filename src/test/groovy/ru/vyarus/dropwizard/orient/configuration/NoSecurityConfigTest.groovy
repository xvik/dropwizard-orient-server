package ru.vyarus.dropwizard.orient.configuration

import com.orientechnologies.orient.object.db.OObjectDatabaseTx
import com.orientechnologies.orient.server.OServerMain
import com.orientechnologies.orient.server.network.protocol.http.ONetworkProtocolHttpDb
import com.orientechnologies.orient.server.network.protocol.http.command.get.OServerCommandGetStaticContent
import io.dropwizard.testing.junit5.DropwizardAppExtension
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport
import org.junit.jupiter.api.extension.ExtendWith
import ru.vyarus.dropwizard.orient.AbstractTest
import ru.vyarus.dropwizard.orient.support.TestApplication
import ru.vyarus.dropwizard.orient.support.TestConfiguration

/**
 * @author Vyacheslav Rusakov
 * @since 25.02.2017
 */
@ExtendWith(DropwizardExtensionsSupport)
class NoSecurityConfigTest extends AbstractTest {
    private static final DropwizardAppExtension<TestConfiguration> EXT = new DropwizardAppExtension<>(
            TestApplication.class,
            "src/test/resources/ru/vyarus/dropwizard/orient/conf/noSecurity.yml"
    )

    def "Check server starts correctly"() {

        when: "accessing started orient server"
        createRemoteDb('nosecurity')
        OObjectDatabaseTx db = new OObjectDatabaseTx('remote:localhost/nosecurity');
        db.open('admin', 'admin');
        db.close()
        then: "all good"
        true

        when: "accessing orient studio"
        int port = OServerMain.server().getListenerByProtocol(ONetworkProtocolHttpDb).getInboundAddr().getPort()
        def data = new URL("http://localhost:$port/studio/").getText()
        then: "all good"
        data != null
    }

}