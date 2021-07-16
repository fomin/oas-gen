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
import org.springframework.http.MediaType;
import org.springframework.http.server.PathContainer;
import org.springframework.http.server.RequestPath;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

public class SimpleHandlerAdapter implements MatchingHandlerAdapter {
    private static final PathPattern pathPattern0 = PathPatternParser.defaultInstance.parse("/path1");
    private static final PathPattern pathPattern1 = PathPatternParser.defaultInstance.parse("/path2/{id}");
    private static final PathPattern pathPattern2 = PathPatternParser.defaultInstance.parse("/path3");
    private static final PathPattern pathPattern3 = PathPatternParser.defaultInstance.parse("/path4");
    private static final PathPattern pathPattern4 = PathPatternParser.defaultInstance.parse("/path5");

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


                String contentType = request.getContentType();
                MediaType mediaType = MediaType.parseMediaType(contentType);
                if (!mediaType.equalsTypeAndSubtype(MediaType.parseMediaType("application/json"))) {
                    throw new UnsupportedOperationException(contentType);
                }
                JsonNode jsonNode = objectMapper.readTree(request.getInputStream());
                com.example.dto.Dto requestBodyDto = com.example.routes.DtoConverter.parse(jsonNode);
                response.setStatus(200);
                response.setContentType("application/json");
                java.lang.String responseBody = operations.simplePost(
                        requestBodyDto
                );
                JsonGenerator jsonGenerator = objectMapper.createGenerator(response.getOutputStream());
                List<? extends ValidationError> validationErrors = io.github.fomin.oasgen.StringConverter.write(jsonGenerator, responseBody);
                jsonGenerator.close();
                if (!validationErrors.isEmpty()) {
                    throw new ValidationException(validationErrors);
                }
                return null;
            }
        }
        PathPattern.PathMatchInfo pathMatchInfo1 = pathPattern1.matchAndExtract(pathContainer);
        if (pathMatchInfo1 != null) {
            if ("GET".equals(request.getMethod())) {
                Map<String, String> uriVariables = pathMatchInfo1.getUriVariables();
                String param0Str = uriVariables.get("id");
                java.lang.String param0 = param0Str != null ? param0Str : null;
                String param1Str = request.getParameter("param1");
                java.lang.String param1 = param1Str != null ? param1Str : null;
                String param2Str = request.getParameter("param2");
                com.example.dto.Param2OfSimpleGet param2 = param2Str != null ? com.example.routes.Param2OfSimpleGetConverter.parseString(param2Str) : null;
                String param3Str = request.getHeader("param3");
                java.time.LocalDate param3 = param3Str != null ? java.time.LocalDate.parse(param3Str) : null;


                response.setStatus(200);
                response.setContentType("application/json");
                com.example.dto.Dto responseBody = operations.simpleGet(
                        param0,
                        param1,
                        param2,
                        param3
                );
                JsonGenerator jsonGenerator = objectMapper.createGenerator(response.getOutputStream());
                List<? extends ValidationError> validationErrors = com.example.routes.DtoConverter.write(jsonGenerator, responseBody);
                jsonGenerator.close();
                if (!validationErrors.isEmpty()) {
                    throw new ValidationException(validationErrors);
                }
                return null;
            }
        }
        PathPattern.PathMatchInfo pathMatchInfo2 = pathPattern2.matchAndExtract(pathContainer);
        if (pathMatchInfo2 != null) {
            if ("POST".equals(request.getMethod())) {

                String param0Str = request.getParameter("param1");
                java.time.LocalDate param0 = param0Str != null ? java.time.LocalDate.parse(param0Str) : null;


                response.setStatus(200);

                operations.testNullableParameter(
                        param0
                );

                return null;
            }
        }
        PathPattern.PathMatchInfo pathMatchInfo3 = pathPattern3.matchAndExtract(pathContainer);
        if (pathMatchInfo3 != null) {
            if ("GET".equals(request.getMethod())) {




                response.setStatus(200);
                response.setContentType("application/octet-stream");
                operations.returnOctetStream(
                        response.getOutputStream()
                );

                return null;
            }
        }
        PathPattern.PathMatchInfo pathMatchInfo4 = pathPattern4.matchAndExtract(pathContainer);
        if (pathMatchInfo4 != null) {
            if ("POST".equals(request.getMethod())) {


                String contentType = request.getContentType();
                MediaType mediaType = MediaType.parseMediaType(contentType);
                if (!mediaType.equalsTypeAndSubtype(MediaType.parseMediaType("application/octet-stream"))) {
                    throw new UnsupportedOperationException(contentType);
                }

                response.setStatus(200);

                operations.sendOctetStream(
                        request.getInputStream()
                );

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
                || pathPattern1.matches(pathContainer)
                || pathPattern2.matches(pathContainer)
                || pathPattern3.matches(pathContainer)
                || pathPattern4.matches(pathContainer);
    }
}
