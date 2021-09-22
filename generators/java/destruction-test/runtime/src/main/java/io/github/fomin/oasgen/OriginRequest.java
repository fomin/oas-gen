package io.github.fomin.oasgen;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class OriginRequest implements Request {
    private final String operationId;
    private final String path;
    private final String method;
    private final Map<String, Parameter<?>> headers;
    private final Map<String, Parameter<?>> params;
    private final Map<String, Parameter<?>> pathParams;
    private final Map<String, Parameter<?>> queryParams;
    private final Parameter<?> body;

    public OriginRequest(
            String operationId,
            String path,
            String method,
            Map<String, Parameter<?>> headers,
            Map<String, Parameter<?>> params,
            Map<String, Parameter<?>> pathParams,
            Map<String, Parameter<?>> queryParams,
            Parameter<?> body) {
        this.operationId = operationId;
        this.path = path;
        this.method = method;
        this.headers = headers;
        this.params = params;
        this.pathParams = pathParams;
        this.queryParams = queryParams;
        this.body = body;
    }

    @Override
    public String toString() {
        return operationId;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public String getMethod() {
        return method;
    }

    @Override
    public Map<String, Object> getHeaders() {
        if (headers == null) {
            return new HashMap<>();
        }
        return headers.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getParam()));
    }

    @Override
    public Map<String, Object> getParams() {
        if (params == null) {
            return new HashMap<>();
        }
        return params.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getParam()));
    }

    @Override
    public Map<String, Object> getPathParams() {
        if (pathParams == null) {
            return new HashMap<>();
        }
        return pathParams.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getParam()));
    }

    @Override
    public Map<String, Object> getQueryParams() {
        if (queryParams == null) {
            return new HashMap<>();
        }
        return queryParams.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getParam()));
    }


    @Override
    public Parameter<?> getBody() {
        return body;
    }
}
