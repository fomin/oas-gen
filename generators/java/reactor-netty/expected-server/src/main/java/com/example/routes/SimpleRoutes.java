package com.example.routes;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.fomin.oasgen.ByteBufConverter;
import io.github.fomin.oasgen.UrlEncoderUtils;
import java.util.Map;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerRoutes;

public abstract class SimpleRoutes implements Consumer<HttpServerRoutes> {
    private final ByteBufConverter byteBufConverter;
    private final String baseUrl;

    protected SimpleRoutes(ObjectMapper objectMapper, String baseUrl) {
        this.byteBufConverter = new ByteBufConverter(objectMapper);
        this.baseUrl = baseUrl;
    }

    @Nonnull
    public abstract Mono<java.lang.String> simplePost(@Nonnull Mono<com.example.dto.Dto> requestBodyMono);

    @Nonnull
    public abstract Mono<com.example.dto.Dto> simpleGet(@Nullable java.lang.String xHeader, @Nonnull java.lang.String id, @Nonnull java.lang.String param1, @Nullable com.example.dto.Param2OfSimpleGet param2);

    @Override
    public final void accept(HttpServerRoutes httpServerRoutes) {
        httpServerRoutes
            .post(baseUrl + "/path1", (request, response) -> {


                Mono<com.example.dto.Dto> requestMono = byteBufConverter.parse(request.receive().aggregate(), jsonNode -> com.example.routes.DtoConverter.parse(jsonNode));
                Mono<java.lang.String> responseMono = simplePost(requestMono);
                return response
                        .status(200)
                        .header("Content-Type", "application/json")
                        .send(byteBufConverter.write(response, responseMono, (jsonGenerator, value) -> io.github.fomin.oasgen.StringConverter.write(jsonGenerator, value)));
            })
            .get(baseUrl + "/path2/{id}", (request, response) -> {
                Map<String, String> queryParams = UrlEncoderUtils.parseQueryParams(request.uri());
                String param0 = request.requestHeaders().get("X-header");
                String param1Str = request.param("id");
                java.lang.String param1 = param1Str != null ? param1Str : null;
                String param2Str = queryParams.get("param1");
                java.lang.String param2 = param2Str != null ? param2Str : null;
                String param3Str = queryParams.get("param2");
                com.example.dto.Param2OfSimpleGet param3 = param3Str != null ? com.example.routes.Param2OfSimpleGetConverter.parseString(param3Str) : null;

                Mono<com.example.dto.Dto> responseMono = simpleGet(param0, param1, param2, param3);
                return response
                        .status(200)
                        .header("Content-Type", "application/json")
                        .send(byteBufConverter.write(response, responseMono, (jsonGenerator, value) -> com.example.routes.DtoConverter.write(jsonGenerator, value)));
            })
        ;
    }
}
