package com.example;

import com.fasterxml.jackson.core.JsonFactory;
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

    protected SimpleRoutes(JsonFactory jsonFactory, String baseUrl) {
        this.byteBufConverter = new ByteBufConverter(jsonFactory);
        this.baseUrl = baseUrl;
    }

    @Nonnull
    public abstract Mono<java.lang.String> create(@Nonnull Mono<com.example.Item> requestBodyMono);

    @Nonnull
    public abstract Mono<java.lang.String> postWithoutRequestBody();

    @Nonnull
    public abstract Mono<com.example.Item> find(@Nonnull java.lang.String param1, @Nullable com.example.Param2OfFind param2);

    @Nonnull
    public abstract Mono<com.example.ComponentItem> get(@Nonnull java.lang.String id);

    @Override
    public final void accept(HttpServerRoutes httpServerRoutes) {
        httpServerRoutes
            .post(baseUrl + "/", (request, response) -> {


                Mono<com.example.Item> requestMono = byteBufConverter.parse(request.receive(), new com.example.Item.Parser());
                Mono<java.lang.String> responseMono = create(requestMono);
                return response
                        .header("Content-Type", "application/json")
                        .send(byteBufConverter.write(response, responseMono, io.github.fomin.oasgen.StringConverter.WRITER));
            })
            .post(baseUrl + "/post-without-request-body", (request, response) -> {



                Mono<java.lang.String> responseMono = postWithoutRequestBody();
                return response
                        .header("Content-Type", "application/json")
                        .send(byteBufConverter.write(response, responseMono, io.github.fomin.oasgen.StringConverter.WRITER));
            })
            .get(baseUrl + "/find", (request, response) -> {
                Map<String, String> queryParams = UrlEncoderUtils.parseQueryParams(request.uri());
                String param0Str = queryParams.get("param1");
                java.lang.String param0 = param0Str != null ? param0Str : null;
                String param1Str = queryParams.get("param2");
                com.example.Param2OfFind param1 = param1Str != null ? com.example.Param2OfFind.parseString(param1Str) : null;

                Mono<com.example.Item> responseMono = find(param0, param1);
                return response
                        .header("Content-Type", "application/json")
                        .send(byteBufConverter.write(response, responseMono, com.example.Item.Writer.INSTANCE));
            })
            .get(baseUrl + "/{id}", (request, response) -> {

                String param0Str = request.param("id");
                java.lang.String param0 = param0Str != null ? param0Str : null;

                Mono<com.example.ComponentItem> responseMono = get(param0);
                return response
                        .header("Content-Type", "application/json")
                        .send(byteBufConverter.write(response, responseMono, com.example.ComponentItem.Writer.INSTANCE));
            })
        ;
    }
}
