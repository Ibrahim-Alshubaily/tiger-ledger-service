package com.alshubaily.fintech.tiger_ledger_service.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DomainNameResolver {
    private static final Logger logger = LoggerFactory.getLogger(DomainNameResolver.class);

    public static String resolveAddress(String host, String port) {
        try {
            InetAddress inet = InetAddress.getByName(host);
            String address = inet.getHostAddress() + ":" + port;
            logger.info("Resolved {} to: {}", host, address);
            return address;
        } catch (UnknownHostException e) {
            logger.error("Failed to resolve {}: {}", host, e.getMessage());
            throw new IllegalStateException("Failed to resolve host: " + host, e);
        }
    }
}
