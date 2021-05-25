package io.github.fomin.oasgen;

import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;

import java.util.Arrays;

public class ComposingRequestCallbacks implements RequestCallbacks {

    private final RequestCallbacks[] requestCallbacks;

    public ComposingRequestCallbacks(RequestCallbacks[] requestCallbacks) {
        this.requestCallbacks = Arrays.copyOf(requestCallbacks, requestCallbacks.length);
    }

    @Override
    public void onRequest(ClientHttpRequest request) {
        for (RequestCallbacks requestCallback : requestCallbacks) {
            requestCallback.onRequest(request);
        }
    }

    @Override
    public void onResponse(ClientHttpResponse response) {
        for (int i = requestCallbacks.length - 1; i >= 0; i--) {
            requestCallbacks[i].onResponse(response);
        }
    }
}
