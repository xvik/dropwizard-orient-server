package ru.vyarus.dropwizard.orient.https

import com.orientechnologies.orient.core.config.OGlobalConfiguration
import groovyx.net.http.ContentEncoding
import groovyx.net.http.ContentType
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.Method
import org.apache.http.client.RedirectStrategy
import org.apache.http.impl.client.AbstractHttpClient
import ru.vyarus.dropwizard.orient.AbstractTest

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
        def builder = new HTTPBuilder(url)
        builder.contentEncoding = ContentEncoding.Type.GZIP
        builder.ignoreSSLIssues()
        builder.get(contentType : ContentType.TEXT).text
    }

    void checkRedirect(String url, String redirectStartsWith) {
        def httpBuilder = new HTTPBuilder(url)
        // Make sure that HttpClient doesn't perform a redirect
        (httpBuilder.client as AbstractHttpClient).setRedirectStrategy([
                getRedirect : { request, response, context -> null},
                isRedirected : { request, response, context -> false}
        ] as RedirectStrategy)

        httpBuilder.ignoreSSLIssues()

        // Execute a GET request and expect a redirect
        httpBuilder.request(Method.GET, ContentType.HTML) {
            req ->
                response.success = { response, reader ->
                    assert response.statusLine.statusCode == 302
                    assert response.headers['Location'].value.startsWith(redirectStartsWith)
                }
                response.failure = { response, reader ->
                    // redirect expected
                    assert false
                }
        }
    }
}
