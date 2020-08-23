package com.example;

import com.fasterxml.jackson.core.JsonFactory;
import io.github.fomin.oasgen.ByteBufConverter;
import io.github.fomin.oasgen.UrlEncoderUtils;
import io.netty.buffer.ByteBuf;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

public class SimpleClient {
    private final ByteBufConverter byteBufConverter;
    private final HttpClient httpClient;

    public SimpleClient(@Nonnull JsonFactory jsonFactory, @Nonnull HttpClient httpClient) {
        this.byteBufConverter = new ByteBufConverter(jsonFactory);
        this.httpClient = httpClient;
    }

    @Nonnull
    public Mono<java.lang.String> create(
            @Nonnull Mono<com.example.Item> item
    ) {
        return create$0(
                item
        );
    }

    private Mono<java.lang.String> create$0(
            Mono<com.example.Item> bodyArg
    ) {
        Flux<ByteBuf> responseByteBufFlux = httpClient
                .post()
                .uri(UrlEncoderUtils.encodeUrl("/"))
                .send((httpClientRequest, nettyOutbound) -> {
                    Mono<ByteBuf> byteBufMono = byteBufConverter.write(nettyOutbound, bodyArg, com.example.Item.Writer.INSTANCE);
                    return nettyOutbound.send(byteBufMono);
                })
                .response((httpClientResponse, byteBufFlux) -> byteBufFlux);
        return byteBufConverter.parse(responseByteBufFlux, io.github.fomin.oasgen.StringConverter.createParser());
    }

    @Nonnull
    public Mono<java.lang.String> postWithoutRequestBody(

    ) {
        return postWithoutRequestBody$0(

        );
    }

    private Mono<java.lang.String> postWithoutRequestBody$0(

    ) {
        Flux<ByteBuf> responseByteBufFlux = httpClient
                .post()
                .uri(UrlEncoderUtils.encodeUrl("/post-without-request-body"))

                .response((httpClientResponse, byteBufFlux) -> byteBufFlux);
        return byteBufConverter.parse(responseByteBufFlux, io.github.fomin.oasgen.StringConverter.createParser());
    }

    @Nonnull
    public Mono<com.example.Item> find(
            @Nonnull java.lang.String param1,
            @Nullable java.lang.String param2,
            @Nullable com.example.EnumItem param3
    ) {
        return find$0(
                param1,
                param2,
                param3
        );
    }

    private Mono<com.example.Item> find$0(
            java.lang.String param0,
            java.lang.String param1,
            com.example.EnumItem param2
    ) {
        Flux<ByteBuf> responseByteBufFlux = httpClient
                .get()
                .uri(UrlEncoderUtils.encodeUrl("/find", "param1", param0, "param2", param1, "param3", param2.strValue))

                .response((httpClientResponse, byteBufFlux) -> byteBufFlux);
        return byteBufConverter.parse(responseByteBufFlux, new com.example.Item.Parser());
    }

    @Nonnull
    public Mono<com.example.Item> get(
            @Nonnull java.lang.String id
    ) {
        return get$0(
                id
        );
    }

    private Mono<com.example.Item> get$0(
            java.lang.String param0
    ) {
        Flux<ByteBuf> responseByteBufFlux = httpClient
                .get()
                .uri(UrlEncoderUtils.encodeUrl("/" + UrlEncoderUtils.encode(param0)))

                .response((httpClientResponse, byteBufFlux) -> byteBufFlux);
        return byteBufConverter.parse(responseByteBufFlux, new com.example.Item.Parser());
    }

}
