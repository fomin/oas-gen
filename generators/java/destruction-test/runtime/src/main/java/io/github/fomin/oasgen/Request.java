package io.github.fomin.oasgen;

import java.util.Map;

public interface Request {
    String getPath();

    String getMethod();

    Map<String, Object> getHeaders();

    Map<String, Object> getParams();

    Map<String, Object> getPathParams();

    Map<String, Object> getQueryParams();

    Parameter<?> getBody();
}
