package com.example.routes;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.fomin.oasgen.ByteBufConverter;
import io.github.fomin.oasgen.UrlEncoderUtils;
import java.util.Map;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import io.netty.buffer.ByteBuf;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.ByteBufFlux;
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
    public abstract Mono<com.example.dto.Dto> simpleGet(@Nonnull java.lang.String id, @Nonnull java.lang.String param1, @Nullable com.example.dto.Param2OfSimpleGet param2, @Nullable java.time.LocalDate param3Header);

    @Nonnull
    public abstract Mono<Void> testNullableParameter(@Nullable java.time.LocalDate param1);

    @Nonnull
    public abstract Flux<ByteBuf> returnOctetStream();

    @Nonnull
    public abstract Mono<Void> sendOctetStream(@Nonnull ByteBufFlux requestBodyFlux);

    @Override
    public final void accept(HttpServerRoutes httpServerRoutes) {
        httpServerRoutes
            .post(baseUrl + "/path1", (request, response) -> {


                Mono<com.example.dto.Dto> requestPublisher = byteBufConverter.parse(
                        request.receive().aggregate(),
                        jsonNode -> com.example.routes.DtoConverter.parse(jsonNode)
                );
                Publisher<ByteBuf> responsePublisher = byteBufConverter.write(
                        response,
                        simplePost(requestPublisher),
                        (jsonGenerator, value) -> io.github.fomin.oasgen.StringConverter.write(jsonGenerator, value)
                );
                return response
                        .status(200)
                        .header("Content-Type", "application/json")
                        .send(responsePublisher);
            })
            .get(baseUrl + "/path2/{id}", (request, response) -> {
                Map<String, String> queryParams = UrlEncoderUtils.parseQueryParams(request.uri());
                String param0Str = request.param("id");
                java.lang.String param0 = param0Str != null ? param0Str : null;
                String param1Str = queryParams.get("param1");
                java.lang.String param1 = param1Str != null ? param1Str : null;
                String param2Str = queryParams.get("param2");
                com.example.dto.Param2OfSimpleGet param2 = param2Str != null ? com.example.routes.Param2OfSimpleGetConverter.parseString(param2Str) : null;
                String param3Str = request.requestHeaders().get("param3-header");
                java.time.LocalDate param3 = param3Str != null ? java.time.LocalDate.parse(param3Str) : null;

                Publisher<ByteBuf> responsePublisher = byteBufConverter.write(
                        response,
                        simpleGet(param0, param1, param2, param3),
                        (jsonGenerator, value) -> com.example.routes.DtoConverter.write(jsonGenerator, value)
                );
                return response
                        .status(200)
                        .header("Content-Type", "application/json")
                        .send(responsePublisher);
            })
            .post(baseUrl + "/path3", (request, response) -> {
                Map<String, String> queryParams = UrlEncoderUtils.parseQueryParams(request.uri());
                String param0Str = queryParams.get("param1");
                java.time.LocalDate param0 = param0Str != null ? java.time.LocalDate.parse(param0Str) : null;

                Publisher<ByteBuf> responsePublisher = testNullableParameter(param0).cast(ByteBuf.class);
                return response
                        .status(200)

                        .send(responsePublisher);
            })
            .get(baseUrl + "/path4", (request, response) -> {



                Publisher<ByteBuf> responsePublisher = returnOctetStream();
                return response
                        .status(200)
                        .header("Content-Type", "application/octet-stream")
                        .send(responsePublisher);
            })
            .post(baseUrl + "/path5", (request, response) -> {


                ByteBufFlux requestPublisher = request.receive();
                Publisher<ByteBuf> responsePublisher = sendOctetStream(requestPublisher).cast(ByteBuf.class);
                return response
                        .status(200)

                        .send(responsePublisher);
            })
        ;
    }
}
