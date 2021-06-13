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
            @Nullable java.time.LocalDate xHeader,
            @Nonnull java.lang.String id,
            @Nonnull java.lang.String param1,
            @Nullable com.example.dto.Param2OfSimpleGet param2
    ) {
        return simpleGet$0(
                xHeader,
                id,
                param1,
                param2
        );
    }

    private Mono<com.example.dto.Dto> simpleGet$0(
            java.time.LocalDate param0,
            java.lang.String param1,
            java.lang.String param2,
            com.example.dto.Param2OfSimpleGet param3
    ) {
        String param1Str = param1 != null ? param1 : null;
        String param2Str = param2 != null ? param2 : null;
        String param3Str = param3 != null ? com.example.routes.Param2OfSimpleGetConverter.writeString(param3) : null;
        return httpClient
                .headers(headers -> headers.add("X-header", param0 != null ? param0.format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE) : null))
                .get()
                .uri(UrlEncoderUtils.encodeUrl("/path2/" + UrlEncoderUtils.encode(param1Str), "param1", param2Str, "param2", param3Str))

                .responseSingle((httpClientResponse, byteBufMono) ->
                        byteBufConverter.parse(byteBufMono, jsonNode -> com.example.routes.DtoConverter.parse(jsonNode))
                );
    }

}
