package com.example;

import com.fasterxml.jackson.core.JsonFactory;
import io.github.fomin.oasgen.ByteBufConverter;
import io.github.fomin.oasgen.UrlEncoderUtils;
import io.netty.buffer.ByteBuf;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

public class SimpleClient {
    private final ByteBufConverter byteBufConverter;
    private final HttpClient httpClient;

    public SimpleClient(JsonFactory jsonFactory, HttpClient httpClient) {
        this.byteBufConverter = new ByteBufConverter(jsonFactory);
        this.httpClient = httpClient;
    }

    public Mono<java.lang.String> create(
            Mono<com.example.Item> item
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
        return byteBufConverter.parse(responseByteBufFlux, io.github.fomin.oasgen.ScalarParser.createStringParser());
    }

    public Mono<com.example.Item> get(
            java.lang.String id
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

    public Mono<com.example.Item> find(
            java.lang.String param1,
            java.lang.String param2
    ) {
        return find$0(
                param1,
                param2
        );
    }

    private Mono<com.example.Item> find$0(
            java.lang.String param0,
            java.lang.String param1
    ) {
        Flux<ByteBuf> responseByteBufFlux = httpClient
                .get()
                .uri(UrlEncoderUtils.encodeUrl("/find", "param1", param0, "param2", param1))

                .response((httpClientResponse, byteBufFlux) -> byteBufFlux);
        return byteBufConverter.parse(responseByteBufFlux, new com.example.Item.Parser());
    }

}
