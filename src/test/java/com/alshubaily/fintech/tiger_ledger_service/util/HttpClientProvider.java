package com.alshubaily.fintech.tiger_ledger_service.util;

import javax.net.ssl.SSLContext;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.core5.ssl.SSLContextBuilder;

public class HttpClientProvider {

    public static CloseableHttpClient getHttpClient() throws Exception {
        SSLContext sslContext =
                SSLContextBuilder.create().loadTrustMaterial((chain, authType) -> true).build();

        return HttpClients.custom()
                .setConnectionManager(
                        PoolingHttpClientConnectionManagerBuilder.create()
                                .setSSLSocketFactory(
                                        SSLConnectionSocketFactoryBuilder.create()
                                                .setSslContext(sslContext)
                                                .setHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                                                .build())
                                .build())
                .build();
    }
}
