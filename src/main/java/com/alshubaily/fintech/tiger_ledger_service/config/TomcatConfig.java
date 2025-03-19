package com.alshubaily.fintech.tiger_ledger_service.config;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.AbstractHttp11Protocol;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TomcatConfig implements WebServerFactoryCustomizer<TomcatServletWebServerFactory> {

    @Override
    public void customize(TomcatServletWebServerFactory factory) {
        factory.addConnectorCustomizers((Connector connector) -> {
            AbstractHttp11Protocol<?> protocol = (AbstractHttp11Protocol<?>) connector.getProtocolHandler();

            protocol.setMaxThreads(500);
            protocol.setMinSpareThreads(50);

            protocol.setMaxConnections(2000);
            protocol.setAcceptCount(1000);

            protocol.setKeepAliveTimeout(5000);
            protocol.setMaxKeepAliveRequests(100);

            protocol.setCompression("on");

            connector.addUpgradeProtocol(new org.apache.coyote.http2.Http2Protocol());
        });
    }
}
