package io.github.fomin.oasgen;

import org.springframework.web.servlet.HandlerAdapter;

import javax.servlet.http.HttpServletRequest;

public interface MatchingHandlerAdapter extends HandlerAdapter {
    boolean matches(HttpServletRequest request);
}
