package io.github.fomin.oasgen.test;

import io.netty.handler.codec.http.HttpHeaders;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.LocalDate;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class BaseServerTest {
    protected static final String CONTEXT_PATH = "/base";
    public static final String DTO_JSON = "{\"property1\":\"value1\"}";
    public static final String POST_RESPONSE_VALUE_JSON = "\"postResponseValue\"";
    public static final byte[] TEST_BYTES = new byte[]{1, 2, 3, 100, 101, 102};

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
                    HttpHeaders responseHeaders = httpClientResponse.responseHeaders();
                    assertEquals("application/json", responseHeaders.get("Content-Type"));
                    return byteBufMono.asString();
                })
                .block();
        assertEquals(POST_RESPONSE_VALUE_JSON, body);
    }

    @Test
    public void testGet() {
        String body = httpClient
                .headers(headers -> headers.add("param3-header", LocalDate.parse("2021-06-01")))
                .get()
                .uri("/path2/idValue?param1=param1Value&param2=value1")
                .responseSingle((httpClientResponse, byteBufMono) -> {
                    assertEquals(OK, httpClientResponse.status());
                    HttpHeaders responseHeaders = httpClientResponse.responseHeaders();
                    assertEquals("application/json", responseHeaders.get("Content-Type"));
                    return byteBufMono.asString();
                })
                .block();
        assertEquals(DTO_JSON, body);
    }

    @Test
    public void testNullableParameter() {
        httpClient
                .post()
                .uri("/path3")
                .responseSingle((httpClientResponse, byteBufMono) -> {
                    assertEquals(OK, httpClientResponse.status());
                    return byteBufMono.then();
                })
                .block();
    }

    @Test
    void testReturnOctetStream() {
        byte[] body = httpClient
                .get()
                .uri("/path4")
                .responseSingle((httpClientResponse, byteBufMono) -> {
                    assertEquals(OK, httpClientResponse.status());
                    HttpHeaders responseHeaders = httpClientResponse.responseHeaders();
                    assertEquals("application/octet-stream", responseHeaders.get("Content-Type"));
                    return byteBufMono.asByteArray();
                })
                .block();
        assertArrayEquals(TEST_BYTES, body);
    }

    @Test
    void testSendOctetStream() {
        httpClient
                .post()
                .uri("/path5")
                .send((httpClientRequest, nettyOutbound) -> {
                    httpClientRequest.addHeader("Content-Type", "application/octet-stream");
                    return nettyOutbound.sendByteArray(Mono.just(TEST_BYTES));
                })
                .responseSingle((httpClientResponse, byteBufMono) -> {
                    assertEquals(OK, httpClientResponse.status());
                    return byteBufMono.then();
                })
                .block();
    }
}
