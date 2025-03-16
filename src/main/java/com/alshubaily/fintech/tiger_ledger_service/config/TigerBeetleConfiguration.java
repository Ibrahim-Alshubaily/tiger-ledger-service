package com.alshubaily.fintech.tiger_ledger_service.config;

import com.tigerbeetle.Client;
import com.tigerbeetle.UInt128;
import com.alshubaily.fintech.tiger_ledger_service.util.DomainNameResolver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TigerBeetleConfiguration {

    @Value("${tigerbeetle.host:tigerbeetle}")
    private String host;

    @Value("${tigerbeetle.port:3000}")
    private String port;

    @Value("${tigerbeetle.cluster:0}")
    private long cluster;

    @Bean
    public Client tigerBeetleClient() {
        String address = DomainNameResolver.resolveAddress(host, port);
        return new Client(UInt128.asBytes(cluster), new String[]{address});
    }
}
