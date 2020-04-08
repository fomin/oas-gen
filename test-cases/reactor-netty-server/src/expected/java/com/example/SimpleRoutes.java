package com.example;

import com.fasterxml.jackson.core.JsonFactory;
import io.github.fomin.oasgen.ByteBufConverter;
import io.github.fomin.oasgen.UrlEncoderUtils;
import java.util.Map;
import java.util.function.Consumer;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerRoutes;

public abstract class SimpleRoutes implements Consumer<HttpServerRoutes> {
    private final ByteBufConverter byteBufConverter;
    private final String baseUrl;

    protected SimpleRoutes(JsonFactory jsonFactory, String baseUrl) {
        this.byteBufConverter = new ByteBufConverter(jsonFactory);
        this.baseUrl = baseUrl;
    }

    public abstract Mono<java.lang.String> create(Mono<com.example.Item> requestBodyMono);

    public abstract Mono<com.example.Item> find(java.lang.String param1, java.lang.String param2);

    public abstract Mono<com.example.Item> get(java.lang.String id);

    @Override
    public final void accept(HttpServerRoutes httpServerRoutes) {
        httpServerRoutes
            .post(baseUrl + "/", (request, response) -> {


                Mono<com.example.Item> requestMono = byteBufConverter.parse(request.receive(), new com.example.Item.Parser());
                Mono<java.lang.String> responseMono = create(requestMono);
                return response.send(byteBufConverter.write(response, responseMono, io.github.fomin.oasgen.ScalarWriter.STRING_WRITER));
            })
            .get(baseUrl + "/find", (request, response) -> {
                Map<String, String> queryParams = UrlEncoderUtils.parseQueryParams(request.uri());
                java.lang.String param0 = queryParams.get("param1");
                java.lang.String param1 = queryParams.get("param2");

                Mono<com.example.Item> responseMono = find(param0, param1);
                return response.send(byteBufConverter.write(response, responseMono, com.example.Item.Writer.INSTANCE));
            })
            .get(baseUrl + "/{id}", (request, response) -> {

                java.lang.String param0 = request.param("id");

                Mono<com.example.Item> responseMono = get(param0);
                return response.send(byteBufConverter.write(response, responseMono, com.example.Item.Writer.INSTANCE));
            })
        ;
    }
}
