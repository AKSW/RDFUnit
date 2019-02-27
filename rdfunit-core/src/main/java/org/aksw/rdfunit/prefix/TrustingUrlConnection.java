package org.aksw.rdfunit.prefix;

import com.google.common.collect.Lists;
import org.aksw.rdfunit.io.format.SerializationFormat;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HttpContext;

import javax.net.ssl.*;
import java.io.IOException;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Can create a 'trusting' SSL connection, accepting anything.
 * Using apache HttpClient deals with redirects automatically.
 * Should only be used for crawling results of the LOV endpoint.
 */
final class TrustingUrlConnection {

    private static final int TIMEOUT = 5;           //default timeout in seconds
    static final String HEADERKEY = "Redirected";   //the Html Header Key used to convey all redirects which were followed

    // a all trusting Trust Manager
    private static TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
        public java.security.cert.X509Certificate[] getAcceptedIssuers() { return null; }
        public void checkClientTrusted(X509Certificate[] certs, String authType) { }
        public void checkServerTrusted(X509Certificate[] certs, String authType) { }

    } };

    /**
     * Captures every redirect captured starting with the initial address.
     * Is used to check whether we are in a redirect cycle.
     */
    static class MyRedirectHandler extends DefaultRedirectStrategy {

        ArrayList<URI> lastRedirectedUri;

        MyRedirectHandler(URI origin){
            lastRedirectedUri = Lists.newArrayList(origin);
        }

        public URI getLocationURI(HttpRequest request, HttpResponse response, HttpContext context) throws ProtocolException {
            lastRedirectedUri.add(super.getLocationURI(request, response, context));
            return lastRedirectedUri.get(lastRedirectedUri.size()-1);
        }
    }

    private final static RequestConfig requestConfig = RequestConfig
        .custom()
        .setSocketTimeout(TIMEOUT * 1000)
        .setConnectTimeout(TIMEOUT * 1000)
        .setCircularRedirectsAllowed(false)
        .build();

    private final static SSLContext ssl;

    static{
        try {
            ssl = SSLContext.getInstance("SSL");
            ssl.init(null, trustAllCerts, null);
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new RuntimeException("Error while initializing SSL context.", e);
        }
    }

    private final static Registry<ConnectionSocketFactory> registry = RegistryBuilder
        .<ConnectionSocketFactory>create()
        .register("https", new SSLSocketFactory(ssl))
        .register("http", new PlainConnectionSocketFactory())
        .build();

    private static final HttpClientConnectionManager poolingConnManager = new PoolingHttpClientConnectionManager(registry);

    /**
     * Will execute a HEAD only request, following redirects, trying to locate an rdf document.
     * @param uri - the initial uri
     * @param format - the expected mime type
     * @return - the final http response
     * @throws IOException
     */
    static HttpResponse executeHeadRequest(URI uri, SerializationFormat format) throws IOException {

        HttpHead headMethod = new HttpHead(uri);
        MyRedirectHandler redirectHandler = new MyRedirectHandler(uri);

        String acceptHeader = format.getMimeType() != null && ! format.getMimeType().trim().isEmpty() ? format.getMimeType() : "*/*";
        CloseableHttpClient httpClient = HttpClientBuilder
            .create()
            .setDefaultRequestConfig(requestConfig)
            .setConnectionManager(poolingConnManager)
            .setSSLContext(ssl)
            .setRedirectStrategy(redirectHandler)
            .setDefaultHeaders(Arrays.asList(
                new BasicHeader("Accept", acceptHeader), //if default request we try to pretend to be a browser, else we accept everything
                new BasicHeader("User-Agent", "Mozilla/5.0"),
                new BasicHeader("Upgrade-Insecure-Requests", "1")       // we are an all trusting client...
            ))
            .build();

        HttpResponse httpResponse = httpClient.execute(headMethod);
        redirectHandler.lastRedirectedUri.forEach(x -> httpResponse.setHeader(HEADERKEY, String.valueOf(x)));
        return httpResponse;
    }
}
