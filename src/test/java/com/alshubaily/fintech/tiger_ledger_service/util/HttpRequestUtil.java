package com.alshubaily.fintech.tiger_ledger_service.util;

import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.io.entity.EntityUtils;

public class HttpRequestUtil {

    public static RequestResult execute(CloseableHttpClient client, HttpUriRequestBase request) throws Exception {
        return client.execute(request, response -> {
            int status = response.getCode();
            String body = response.getEntity() != null
                    ? EntityUtils.toString(response.getEntity())
                    : "";
            return new RequestResult(status, body);
        });
    }

    public record RequestResult(int status, String body) {}
}
