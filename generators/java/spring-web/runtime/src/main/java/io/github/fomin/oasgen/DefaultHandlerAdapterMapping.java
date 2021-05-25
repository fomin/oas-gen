package io.github.fomin.oasgen;

import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerMapping;

public class DefaultHandlerAdapterMapping implements HandlerMapping {

    private final MatchingHandlerAdapter handlerAdapter;

    public DefaultHandlerAdapterMapping(MatchingHandlerAdapter handlerAdapter) {
        this.handlerAdapter = handlerAdapter;
    }

    @Override
    public HandlerExecutionChain getHandler(javax.servlet.http.HttpServletRequest request) {
        if (handlerAdapter.matches(request)) {
            return new HandlerExecutionChain(handlerAdapter);
        } else {
            return null;
        }
    }

}
