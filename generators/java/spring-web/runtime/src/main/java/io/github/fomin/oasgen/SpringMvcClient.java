package io.github.fomin.oasgen;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
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
import java.util.List;
import java.util.function.Function;

public final class SpringMvcClient {

    private final ClientHttpRequestFactory requestFactory;
    private final ResponseErrorHandler errorHandler;
    private final ObjectMapper objectMapper;
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

    public <T, R> R doRequest(
            URI uri,
            HttpMethod httpMethod,
            T requestBody,
            ValueWriter<T> requestValueWriter,
            Function<JsonNode, R> parseFunction
    ) {
        ClientHttpRequest request;
        ClientHttpResponse response = null;
        try {
            request = requestFactory.createRequest(uri, httpMethod);
            HttpHeaders headers = request.getHeaders();
            requestCallbacks.onRequest(request);
            if (requestBody != null) {
                headers.setContentType(MediaType.APPLICATION_JSON);
                OutputStream body = request.getBody();
                JsonGenerator jsonGenerator = objectMapper.createGenerator(body);
                List<? extends ValidationError> errors = requestValueWriter.write(jsonGenerator, requestBody);
                if (!errors.isEmpty()) {
                    throw new ValidationException(errors);
                }
                jsonGenerator.close();
            }
            response = request.execute();
            requestCallbacks.onResponse(response);
            if (errorHandler.hasError(response)) {
                errorHandler.handleError(uri, httpMethod, response);
            }
            if (parseFunction != null) {
                JsonNode responseJsonNode = objectMapper.readTree(response.getBody());
                return parseFunction.apply(responseJsonNode);
            } else {
                return null;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

}
