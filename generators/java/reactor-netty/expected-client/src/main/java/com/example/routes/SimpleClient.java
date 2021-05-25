package com.example.routes;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.fomin.oasgen.ByteBufConverter;
import io.github.fomin.oasgen.UrlEncoderUtils;
import io.netty.buffer.ByteBuf;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

public class SimpleClient {
    private final ByteBufConverter byteBufConverter;
    private final HttpClient httpClient;

    public SimpleClient(@Nonnull ObjectMapper objectMapper, @Nonnull HttpClient httpClient) {
        this.byteBufConverter = new ByteBufConverter(objectMapper);
        this.httpClient = httpClient;
    }

    @Nonnull
    public Mono<java.lang.String> simplePost(
            @Nonnull Mono<com.example.dto.Dto> dto
    ) {
        return simplePost$0(
                dto
        );
    }

    private Mono<java.lang.String> simplePost$0(
            Mono<com.example.dto.Dto> bodyArg
    ) {

        return httpClient
                .post()
                .uri(UrlEncoderUtils.encodeUrl("/path1"))
                .send((httpClientRequest, nettyOutbound) -> {
                    Mono<ByteBuf> byteBufMono = byteBufConverter.write(nettyOutbound, bodyArg, (jsonGenerator, value) -> com.example.routes.DtoConverter.write(jsonGenerator, value));
                    return nettyOutbound.send(byteBufMono);
                })
                .responseSingle((httpClientResponse, byteBufMono) ->
                        byteBufConverter.parse(byteBufMono, jsonNode -> io.github.fomin.oasgen.StringConverter.parse(jsonNode))
                );
    }

    @Nonnull
    public Mono<com.example.dto.Dto> simpleGet(
            @Nonnull java.lang.String id,
            @Nonnull java.lang.String param1,
            @Nullable com.example.dto.Param2OfSimpleGet param2
    ) {
        return simpleGet$0(
                id,
                param1,
                param2
        );
    }

    private Mono<com.example.dto.Dto> simpleGet$0(
            java.lang.String param0,
            java.lang.String param1,
            com.example.dto.Param2OfSimpleGet param2
    ) {
        String param0Str = param0 != null ? param0 : null;
        String param1Str = param1 != null ? param1 : null;
        String param2Str = param2 != null ? com.example.routes.Param2OfSimpleGetConverter.writeString(param2) : null;
        return httpClient
                .get()
                .uri(UrlEncoderUtils.encodeUrl("/path2/" + UrlEncoderUtils.encode(param0Str), "param1", param1Str, "param2", param2Str))

                .responseSingle((httpClientResponse, byteBufMono) ->
                        byteBufConverter.parse(byteBufMono, jsonNode -> com.example.routes.DtoConverter.parse(jsonNode))
                );
    }

}
