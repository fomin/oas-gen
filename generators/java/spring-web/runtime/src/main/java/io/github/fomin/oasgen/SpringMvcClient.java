package io.github.fomin.oasgen;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.function.Consumer;

public final class SpringMvcClient {

    private final ClientHttpRequestFactory requestFactory;
    private final ResponseErrorHandler errorHandler;
    public final ObjectMapper objectMapper;
    private final RequestCallbacks requestCallbacks;

    public SpringMvcClient(
            ClientHttpRequestFactory requestFactory,
            ResponseErrorHandler errorHandler,
            ObjectMapper objectMapper,
            RequestCallbacks requestCallbacks
    ) {
        this.requestFactory = requestFactory;
        this.errorHandler = errorHandler;
        this.objectMapper = objectMapper;
        this.requestCallbacks = requestCallbacks;
    }

    public <R> R doRequest(
            URI uri,
            HttpMethod httpMethod,
            Consumer<HttpHeaders> headersConsumer,
            RequestConsumer requestBodyConsumer,
            ResponseFunction<R> responseBodyConsumer
    ) {
        try {
            ClientHttpRequest request = requestFactory.createRequest(uri, httpMethod);
            HttpHeaders headers = request.getHeaders();
            if (headersConsumer != null) {
                headersConsumer.accept(headers);
            }
            requestCallbacks.onRequest(request);
            if (requestBodyConsumer != null) {
                headers.setContentType(MediaType.parseMediaType(requestBodyConsumer.contentType));
                try (OutputStream requestBody = request.getBody()) {
                    requestBodyConsumer.consumer.accept(requestBody);
                }
            }
            try (ClientHttpResponse response = request.execute()) {
                requestCallbacks.onResponse(response);
                if (errorHandler.hasError(response)) {
                    errorHandler.handleError(uri, httpMethod, response);
                }
                if (responseBodyConsumer != null) {
                    MediaType contentType = response.getHeaders().getContentType();
                    if (!MediaType.parseMediaType(responseBodyConsumer.contentType).isCompatibleWith(contentType)) {
                        throw new UnsupportedOperationException("" + contentType);
                    }
                    return responseBodyConsumer.function.accept(response.getBody());
                } else {
                    return null;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
