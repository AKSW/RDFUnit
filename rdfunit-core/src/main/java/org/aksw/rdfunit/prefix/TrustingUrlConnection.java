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
 * TODO
 */
final class TrustingUrlConnection {

    private static final int TIMEOUT = 5;
    private static final String DEFAULTBROWSERACCEPT = "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8";
    public static final String HEADERKEY = "Redirected";


    private static TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
        public java.security.cert.X509Certificate[] getAcceptedIssuers() { return null; }
        public void checkClientTrusted(X509Certificate[] certs, String authType) { }
        public void checkServerTrusted(X509Certificate[] certs, String authType) { }

    } };

    static class MyRedirectHandler extends DefaultRedirectStrategy {

        ArrayList<URI> lastRedirectedUri;

        public MyRedirectHandler(URI origin){
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

    static HttpResponse executeHeadRequest(URI uri, SerializationFormat format) throws IOException {

        HttpHead headMethod = new HttpHead(uri);
        MyRedirectHandler redirectHandler = new MyRedirectHandler(uri);

        CloseableHttpClient httpClient = HttpClientBuilder
            .create()
            .setDefaultRequestConfig(requestConfig)
            .setConnectionManager(poolingConnManager)
            .setSSLContext(ssl)
            .setRedirectStrategy(redirectHandler)
            .setDefaultHeaders(Arrays.asList(
                new BasicHeader("Accept", format.getHeaderType().contains("text/html") ? DEFAULTBROWSERACCEPT : "*/*"), //if default request we try to pretend to be a browser, else we accept everything
                new BasicHeader("Content-Type", format.getHeaderType()),
                new BasicHeader("User-Agent", "Mozilla/5.0"),
                new BasicHeader("Upgrade-Insecure-Requests", "1")       // we are an all trusting client...
            ))
            .build();

        HttpResponse httpResponse = httpClient.execute(headMethod);
        redirectHandler.lastRedirectedUri.forEach(x -> httpResponse.setHeader(HEADERKEY, String.valueOf(x)));
        return httpResponse;
    }
}
