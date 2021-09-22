package io.github.fomin.oasgen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MutatedRequest implements Request {
    private final Request origin;
    private final Mutator mutator;
    private final Map<String, Object> headers = new HashMap<>();
    private final Map<String, Object> params = new HashMap<>();
    private final Map<String, Object> queryParams = new HashMap<>();
    private final Map<String, Object> pathParams = new HashMap<>();

    private StringBuilder descriptionBuilder = new StringBuilder();

    public MutatedRequest(Request origin, Mutator mutator) {
        this.origin = origin;
        this.mutator = mutator;
        this.headers.putAll(origin.getHeaders());
        this.params.putAll(origin.getParams());
        this.queryParams.putAll(origin.getQueryParams());
        this.pathParams.putAll(origin.getPathParams());
    }

    @Override
    public String getPath() {
        return origin.getPath();
    }

    @Override
    public String getMethod() {
        return origin.getMethod();
    }

    @Override
    public Map<String, Object> getHeaders() {
        return headers;
    }

    @Override
    public Map<String, Object> getParams() {
        return origin.getParams();
    }

    @Override
    public Map<String, Object> getPathParams() {
        return origin.getPathParams();
    }

    @Override
    public Map<String, Object> getQueryParams() {
        return queryParams;
    }

    @Override
    public Parameter<?> getBody() {
        return origin.getBody();
    }

    @Override
    public String toString() {
        return origin.toString() + " [" + mutator.toString() + "] " + descriptionBuilder.toString();
    }

    public void addHeader(String key, String value) {
        if (headers.containsKey(key)) {
            Object currentValue = headers.get(key);
            List<Object> values = new ArrayList<>();
            if (currentValue instanceof List) {
                values.addAll((List<?>)currentValue);
            } else {
                values.add(currentValue);
            }
            values.add(value);
            headers.put(key, values);
        } else {
            headers.put(key, value);
        }
        descriptionBuilder.append("with new header '" + key + "'");
    }

    public void removeHeader(String key) {
        headers.remove(key);
        descriptionBuilder.append("remove header '" + key + "'");
    }

    public void addQueryParam(String key, String value) {
        queryParams.put(key, value);
        descriptionBuilder.append("with new query param '" + key + "'");
    }

    public void removeQueryParam(String key) {
        queryParams.remove(key);
        descriptionBuilder.append("remove query param '" + key + "'");
    }

    public void updatePathParam(String key, String value) {
        pathParams.put(key, value);
    }
}
