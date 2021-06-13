package io.github.fomin.oasgen.test;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.LocalDate;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class BaseServerTest {
    protected static final String CONTEXT_PATH = "/base";
    public static final String DTO_JSON = "{\"property1\":\"value1\"}";
    public static final LocalDate DATE = LocalDate.now();
    public static final String POST_RESPONSE_VALUE_JSON = "\"postResponseValue\"";

    private final HttpClient httpClient;

    public BaseServerTest(int port) {
        httpClient = HttpClient.create().baseUrl("http://localhost:" + port + CONTEXT_PATH);
    }

    @Test
    public void testPost() {
        String body = httpClient
                .post()
                .uri("/path1")
                .send((httpClientRequest, nettyOutbound) -> {
                    httpClientRequest.addHeader("Content-Type", "application/json");
                    return nettyOutbound.sendString(Mono.just(DTO_JSON));
                })
                .responseSingle((httpClientResponse, byteBufMono) -> {
                    assertEquals(OK, httpClientResponse.status());
                    return byteBufMono.asString();
                })
                .block();
        assertEquals(POST_RESPONSE_VALUE_JSON, body);
    }

    @Test
    public void testGet() {
        String body = httpClient
                .headers(headers -> headers.add("X-Header", DATE))
                .get()
                .uri("/path2/idValue?param1=param1Value&param2=value1")
                .responseSingle((httpClientResponse, byteBufMono) -> {
                    assertEquals(OK, httpClientResponse.status());
                    return byteBufMono.asString();
                })
                .block();
        assertEquals(DTO_JSON, body);
    }

}
