/*
 *  Copyright (c) 2025 Dr. Martin Rösel <opensource@roesel.at>
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package at.roesel.oadataprocessor.services.common;

import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.client5.http.ssl.TrustAllStrategy;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.*;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.List;

public class RestClient {

    public final static String clientName = "Austrian Open Access Datahub";

    private final Logger logger = LoggerFactory.getLogger(RestClient.class);

    protected final RestTemplate restTemplate;

    // @param proxyConfig url:hostname:port,
    // url of server for which the proxy shall be active, hostname and port of Proxy
    // e.g. pure.testuni.at:localhost:3128
    public RestClient(String proxyConfig) {
        try {
            HttpClientBuilder httpClientBuilder = HttpClients.custom()
                    .setConnectionManager(PoolingHttpClientConnectionManagerBuilder.create()
                            .setSSLSocketFactory(SSLConnectionSocketFactoryBuilder.create()
                                    .setSslContext(SSLContextBuilder.create()
                                            .loadTrustMaterial(TrustAllStrategy.INSTANCE)
                                            .build())
                                    .setHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                                    .build())
                            .build());
            // proxy selector may be needed for local development, if access to the server is only allowed for specific IPs
            if (proxyConfig != null && !proxyConfig.isEmpty()) {
                httpClientBuilder.setProxySelector(createProxySelector(proxyConfig));
            }
            HttpClient httpClient = httpClientBuilder.build();

            HttpComponentsClientHttpRequestFactory requestFactory
                    = new HttpComponentsClientHttpRequestFactory();
            requestFactory.setHttpClient(httpClient);

            restTemplate = new RestTemplate(requestFactory);

        } catch (KeyManagementException | KeyStoreException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public RestClient() {
        this(null);
    }

    private ProxySelector createProxySelector(String proxyConfig) {

        ProxyResult result = createProxy(proxyConfig);

        return new ProxySelector() {
            @Override
            public List<Proxy> select(URI uri) {
                if (uri.getHost().equalsIgnoreCase(result.getUrl())) {
                    return Collections.singletonList(result.getProxy());
                }
                return Collections.singletonList(Proxy.NO_PROXY);
            }

            @Override
            public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
                logger.error("Proxy connection failed: {}", ioe.getMessage());
            }
        };
    }

    // Create a simple class to hold both the URL and Proxy
    private static class ProxyResult {
        private String url;
        private Proxy proxy;

        public ProxyResult(String url, Proxy proxy) {
            this.url = url;
            this.proxy = proxy;
        }

        public String getUrl() {
            return url;
        }

        public Proxy getProxy() {
            return proxy;
        }
    }

    private static ProxyResult createProxy(String proxyConfig) {
        // Split the string into parts
        String[] parts = proxyConfig.split(":");

        // Extract the URL, hostname, and port from the parts
        String url = parts[0];
        String hostname = parts[1];
        int port = Integer.parseInt(parts[2]);

        // Create the Proxy object
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(hostname, port));

        // Return both the URL and Proxy as a ProxyResult object
        return new ProxyResult(url, proxy);
    }

}
