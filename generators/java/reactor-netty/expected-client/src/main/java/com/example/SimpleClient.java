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
    public Mono<java.lang.String> simplePost(
            @Nonnull Mono<com.example.Dto> dto
    ) {
        return simplePost$0(
                dto
        );
    }

    private Mono<java.lang.String> simplePost$0(
            Mono<com.example.Dto> bodyArg
    ) {

        Flux<ByteBuf> responseByteBufFlux = httpClient
                .post()
                .uri(UrlEncoderUtils.encodeUrl("/path1"))
                .send((httpClientRequest, nettyOutbound) -> {
                    Mono<ByteBuf> byteBufMono = byteBufConverter.write(nettyOutbound, bodyArg, com.example.Dto.Writer.INSTANCE);
                    return nettyOutbound.send(byteBufMono);
                })
                .response((httpClientResponse, byteBufFlux) -> byteBufFlux);
        return byteBufConverter.parse(responseByteBufFlux, io.github.fomin.oasgen.StringConverter.createParser());
    }

    @Nonnull
    public Mono<com.example.Dto> simpleGet(
            @Nonnull java.lang.String id,
            @Nonnull java.lang.String param1,
            @Nullable com.example.Param2OfSimpleGet param2
    ) {
        return simpleGet$0(
                id,
                param1,
                param2
        );
    }

    private Mono<com.example.Dto> simpleGet$0(
            java.lang.String param0,
            java.lang.String param1,
            com.example.Param2OfSimpleGet param2
    ) {
        String param0Str = param0 != null ? param0 : null;
        String param1Str = param1 != null ? param1 : null;
        String param2Str = param2 != null ? com.example.Param2OfSimpleGet.writeString(param2) : null;
        Flux<ByteBuf> responseByteBufFlux = httpClient
                .get()
                .uri(UrlEncoderUtils.encodeUrl("/path2/" + UrlEncoderUtils.encode(param0Str), "param1", param1Str, "param2", param2Str))

                .response((httpClientResponse, byteBufFlux) -> byteBufFlux);
        return byteBufConverter.parse(responseByteBufFlux, new com.example.Dto.Parser());
    }

}
