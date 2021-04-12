package io.github.fomin.oasgen;

import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerMapping;

public class DefaultHandlerAdapterMapping implements HandlerMapping {

    private final HandlerAdapter handlerAdapter;
    private final String baseUrl;

    public DefaultHandlerAdapterMapping(HandlerAdapter handlerAdapter, String baseUrl) {
        this.handlerAdapter = handlerAdapter;
        this.baseUrl = baseUrl;
    }

    @Override
    public HandlerExecutionChain getHandler(javax.servlet.http.HttpServletRequest request) {
        if (request.getServletPath().startsWith(baseUrl)) {
            return new HandlerExecutionChain(handlerAdapter);
        } else {
            return null;
        }
    }

}
