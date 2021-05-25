package io.github.fomin.oasgen;

import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;

public interface RequestCallbacks {
    void onRequest(ClientHttpRequest request);

    void onResponse(ClientHttpResponse response);
}
