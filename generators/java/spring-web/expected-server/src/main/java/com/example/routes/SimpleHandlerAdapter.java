package com.example.routes;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.fomin.oasgen.MatchingHandlerAdapter;
import io.github.fomin.oasgen.ValidationError;
import io.github.fomin.oasgen.ValidationException;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.server.PathContainer;
import org.springframework.http.server.RequestPath;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

public class SimpleHandlerAdapter implements MatchingHandlerAdapter {
    private static final PathPattern pathPattern0 = PathPatternParser.defaultInstance.parse("/path1");
    private static final PathPattern pathPattern1 = PathPatternParser.defaultInstance.parse("/path2/{id}");

    private final String baseUrl;
    private final com.example.routes.SimpleOperations operations;
    private final ObjectMapper objectMapper;

    public SimpleHandlerAdapter(
            String baseUrl,
            com.example.routes.SimpleOperations operations,
            ObjectMapper objectMapper
    ) {
        this.baseUrl = baseUrl;
        this.operations = operations;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean supports(@NonNull Object handler) {
        return handler == this;
    }

    @Override
    public ModelAndView handle(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull Object handler
    ) throws Exception {
        PathContainer pathContainer = RequestPath.parse(request.getServletPath(), baseUrl).pathWithinApplication();
        PathPattern.PathMatchInfo pathMatchInfo0 = pathPattern0.matchAndExtract(pathContainer);
        if (pathMatchInfo0 != null) {
            if ("POST".equals(request.getMethod())) {



                com.example.dto.Dto requestBodyDto;
                if ("application/json".equals(request.getContentType())) {
                    JsonNode jsonNode = objectMapper.readTree(request.getInputStream());
                    requestBodyDto = com.example.routes.DtoConverter.parse(jsonNode);
                } else {
                    throw new UnsupportedOperationException(request.getContentType());
                }
                java.lang.String responseBody = operations.simplePost(
                        requestBodyDto
                );
                response.setContentType("application/json");
                JsonGenerator jsonGenerator = objectMapper.createGenerator(response.getOutputStream());
                List<? extends ValidationError> validationErrors = io.github.fomin.oasgen.StringConverter.write(jsonGenerator, responseBody);
                jsonGenerator.close();
                if (!validationErrors.isEmpty()) {
                    throw new ValidationException(validationErrors);
                }
                response.setStatus(200);
                return null;
            }
        }
        PathPattern.PathMatchInfo pathMatchInfo1 = pathPattern1.matchAndExtract(pathContainer);
        if (pathMatchInfo1 != null) {
            if ("GET".equals(request.getMethod())) {
                java.lang.String param0 = request.getHeader("X-header");
                Map<String, String> uriVariables = pathMatchInfo1.getUriVariables();
                java.lang.String param1 = uriVariables.get("id");
                java.lang.String param2 = request.getParameter("param1");
                com.example.dto.Param2OfSimpleGet param3 = com.example.routes.Param2OfSimpleGetConverter.parseString(request.getParameter("param2"));

                com.example.dto.Dto responseBody = operations.simpleGet(
                        param0,
                        param1,
                        param2,
                        param3
                );
                response.setContentType("application/json");
                JsonGenerator jsonGenerator = objectMapper.createGenerator(response.getOutputStream());
                List<? extends ValidationError> validationErrors = com.example.routes.DtoConverter.write(jsonGenerator, responseBody);
                jsonGenerator.close();
                if (!validationErrors.isEmpty()) {
                    throw new ValidationException(validationErrors);
                }
                response.setStatus(200);
                return null;
            }
        }
        response.setStatus(404);
        return null;
    }

    @Override
    public long getLastModified(
            @NonNull HttpServletRequest request,
            @NonNull Object handler
    ) {
        return 0;
    }

    @Override
    public boolean matches(HttpServletRequest request) {
        if (!request.getServletPath().startsWith(baseUrl)) {
            return false;
        }
        PathContainer pathContainer = RequestPath.parse(request.getServletPath(), baseUrl).pathWithinApplication();
        return pathPattern0.matches(pathContainer)
                || pathPattern1.matches(pathContainer);
    }
}
