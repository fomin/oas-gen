package io.github.fomin.oasgen;

import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;

public class EmptyRequestCallbacks implements RequestCallbacks {

    public static RequestCallbacks INSTANCE = new EmptyRequestCallbacks();

    @Override
    public void onRequest(ClientHttpRequest request) {
        // do nothing
    }

    @Override
    public void onResponse(ClientHttpResponse response) {
        // do nothing
    }
}
