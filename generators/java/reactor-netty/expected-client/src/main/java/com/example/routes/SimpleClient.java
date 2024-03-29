package com.example.routes;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.fomin.oasgen.ByteBufConverter;
import io.github.fomin.oasgen.UrlEncoderUtils;
import io.netty.buffer.ByteBuf;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import io.netty.handler.codec.http.HttpResponseStatus;
import reactor.core.publisher.Flux;
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
                .headers(headers -> {
                    headers.set("Content-Type", "application/json");
                })
                .post()
                .uri(UrlEncoderUtils.encodeUrl("/path1"))
                .send((httpClientRequest, nettyOutbound) -> {
                    Mono<ByteBuf> byteBufMono = byteBufConverter.write(nettyOutbound, bodyArg, (jsonGenerator, value) -> com.example.routes.DtoConverter.write(jsonGenerator, value));
                    return nettyOutbound.send(byteBufMono);
                })
                .responseSingle((httpClientResponse, byteBufMono) -> {
                    HttpResponseStatus httpResponseStatus = httpClientResponse.status();
                    if (httpResponseStatus.code() == 200) {
                        return byteBufConverter.parse(byteBufMono, jsonNode -> io.github.fomin.oasgen.StringConverter.parse(jsonNode));
                    } else {
                        return Mono.error(new RuntimeException(httpResponseStatus.toString()));
                    }
                });
    }

    @Nonnull
    public Mono<com.example.dto.Dto> simpleGet(
            @Nonnull java.lang.String id,
            @Nonnull java.lang.String param1,
            @Nullable com.example.dto.Param2OfSimpleGet param2,
            @Nullable java.time.LocalDate param3Header
    ) {
        return simpleGet$0(
                id,
                param1,
                param2,
                param3Header
        );
    }

    private Mono<com.example.dto.Dto> simpleGet$0(
            java.lang.String param0,
            java.lang.String param1,
            com.example.dto.Param2OfSimpleGet param2,
            java.time.LocalDate param3
    ) {
        String param0Str = param0 != null ? param0 : null;
        String param1Str = param1 != null ? param1 : null;
        String param2Str = param2 != null ? com.example.routes.Param2OfSimpleGetConverter.writeString(param2) : null;
        String param3Str = param3 != null ? param3.format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE) : null;
        return httpClient
                .headers(headers -> {
                    if (param3Str != null) {
                        headers.set("param3-header", param3Str);
                    }
                })
                .get()
                .uri(UrlEncoderUtils.encodeUrl("/path2/" + UrlEncoderUtils.encode(param0Str), "param1", param1Str, "param2", param2Str))

                .responseSingle((httpClientResponse, byteBufMono) -> {
                    HttpResponseStatus httpResponseStatus = httpClientResponse.status();
                    if (httpResponseStatus.code() == 200) {
                        return byteBufConverter.parse(byteBufMono, jsonNode -> com.example.routes.DtoConverter.parse(jsonNode));
                    } else {
                        return Mono.error(new RuntimeException(httpResponseStatus.toString()));
                    }
                });
    }

    @Nonnull
    public Mono<java.lang.Void> testNullableParameter(
            @Nullable java.time.LocalDate param1
    ) {
        return testNullableParameter$0(
                param1
        );
    }

    private Mono<java.lang.Void> testNullableParameter$0(
            java.time.LocalDate param0
    ) {
        String param0Str = param0 != null ? param0.format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE) : null;
        return httpClient

                .post()
                .uri(UrlEncoderUtils.encodeUrl("/path3", "param1", param0Str))

                .response()
                .handle((httpClientResponse, sink) -> {
                    HttpResponseStatus httpResponseStatus = httpClientResponse.status();
                    if (httpResponseStatus.code() == 200) {
                        sink.complete();
                    } else {
                        sink.error(new RuntimeException(httpResponseStatus.toString()));
                    }
                });
    }

    @Nonnull
    public Flux<ByteBuf> returnOctetStream(

    ) {
        return returnOctetStream$0(

        );
    }

    private Flux<ByteBuf> returnOctetStream$0(

    ) {

        return httpClient

                .get()
                .uri(UrlEncoderUtils.encodeUrl("/path4"))

                .response((httpClientResponse, byteBufFlux) -> {
                    HttpResponseStatus httpResponseStatus = httpClientResponse.status();
                    if (httpResponseStatus.code() == 200) {
                        return byteBufFlux;
                    } else {
                        return Flux.error(new RuntimeException(httpResponseStatus.toString()));
                    }
                });
    }

    @Nonnull
    public Mono<java.lang.Void> sendOctetStream(
            @Nonnull Flux<ByteBuf> requestBodyFlux
    ) {
        return sendOctetStream$0(
                requestBodyFlux
        );
    }

    private Mono<java.lang.Void> sendOctetStream$0(
            @Nonnull Flux<ByteBuf> requestBodyFlux
    ) {

        return httpClient
                .headers(headers -> {
                    headers.set("Content-Type", "application/octet-stream");
                })
                .post()
                .uri(UrlEncoderUtils.encodeUrl("/path5"))
                .send((httpClientRequest, nettyOutbound) -> {
                    return nettyOutbound.send(requestBodyFlux);
                })
                .response()
                .handle((httpClientResponse, sink) -> {
                    HttpResponseStatus httpResponseStatus = httpClientResponse.status();
                    if (httpResponseStatus.code() == 200) {
                        sink.complete();
                    } else {
                        sink.error(new RuntimeException(httpResponseStatus.toString()));
                    }
                });
    }

}
