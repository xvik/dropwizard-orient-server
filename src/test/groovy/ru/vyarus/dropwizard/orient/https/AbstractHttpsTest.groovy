package ru.vyarus.dropwizard.orient.https

import com.orientechnologies.orient.core.config.OGlobalConfiguration
import org.glassfish.jersey.client.ClientProperties
import org.glassfish.jersey.client.filter.EncodingFeature
import org.glassfish.jersey.message.GZipEncoder
import ru.vyarus.dropwizard.orient.AbstractTest

import jakarta.ws.rs.WebApplicationException
import jakarta.ws.rs.client.Client
import jakarta.ws.rs.client.ClientBuilder
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response

/**
 * @author Vyacheslav Rusakov
 * @since 27.08.2017
 */
abstract class AbstractHttpsTest extends AbstractTest {

    void setup() {
        // override trusted certs
        System.setProperty("javax.net.ssl.trustStore", "src/test/resources/ru/vyarus/dropwizard/orient/https/example.keystore");
        System.setProperty("javax.net.ssl.trustStorePassword ", "example");
    }

    void cleanup() {
        // must be switched off or simple binary connections would be impossible
        OGlobalConfiguration.CLIENT_USE_SSL.setValue(false);
    }

    String getGzip(String url) {
        Client client = ClientBuilder.newBuilder()
                .hostnameVerifier((hostname, session) -> true)
                .property(ClientProperties.FOLLOW_REDIRECTS, true)
                .build()

        Response res = client.target(url)
                .register(new EncodingFeature(GZipEncoder))
                .request(MediaType.TEXT_PLAIN_TYPE)
                .acceptEncoding("gzip")
                .get()

        if (res.status == 302) {
            // automatic redirect does not work for unknown reason
            return client.target(res.getHeaderString("Location"))
                    .register(new EncodingFeature(GZipEncoder))
                    .request(MediaType.TEXT_PLAIN_TYPE)
                    .acceptEncoding("gzip")
                    .get(String.class)
        }
        if (res.status != 200) {
            throw new WebApplicationException("Error: " + res.getStatus())
        }
        return res.readEntity(String.class)
    }

    void checkRedirect(String url, String redirectStartsWith) {
        Client client = ClientBuilder.newBuilder()
                .hostnameVerifier((hostname, session) -> true).build();
        Response response = client.target(url)
                .property(ClientProperties.FOLLOW_REDIRECTS, false)
                .request(MediaType.TEXT_HTML_TYPE)
                .get()

        assert response.status == 302
        assert response.getHeaderString('Location').startsWith(redirectStartsWith)
    }
}
