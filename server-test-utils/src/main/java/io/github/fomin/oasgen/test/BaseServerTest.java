package io.github.fomin.oasgen.test;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class BaseServerTest {
    protected static final String CONTEXT_PATH = "/base";
    private static final String TEST_ITEM_STR = "{\"commonProperty1\":\"common property 1 value\",\"property1\":\"property 1 value\",\"property2\":{\"commonProperty1\":\"inner common property 1 value\",\"property21\":\"property 21 value\",\"property22\":\"value1\"},\"decimalProperty\":\"1\",\"localDateTimeProperty\":\"2020-01-01T01:01:00\",\"stringArrayProperty\":[\"array value 1\",\"array value 2\"],\"dateTimeArrayProperty\":[\"2020-11-10T01:01:01+01:00\"],\"mapProperty\":{\"key 1\":10.0},\"dateTimeMapProperty\":{\"key 1\":\"2020-11-10T01:01:01+01:00\"},\"true\":{\"property1\":\"property 1 value\"},\"1 with space-and+other_çhars\":{\"property1\":\"property 1 value\"}}";
    private static final String TEST_COMPONENT_ITEM_STR = "{}";

    private final HttpClient httpClient;

    public BaseServerTest(int port) {
        httpClient = HttpClient.create().baseUrl("http://localhost:" + port + CONTEXT_PATH);
    }

    @Test
    public void testFind() {
        String body = httpClient
                .get()
                .uri("/find?param1=param1Value&param2=value2")
                .responseSingle((httpClientResponse, byteBufMono) -> {
                    assertEquals(OK, httpClientResponse.status());
                    return byteBufMono.asString();
                })
                .block();
        assertEquals(TEST_ITEM_STR, body);
    }

    @Test
    public void testGet() {
        String body = httpClient
                .get()
                .uri("/idValue")
                .responseSingle((httpClientResponse, byteBufMono) -> {
                    assertEquals(OK, httpClientResponse.status());
                    return byteBufMono.asString();
                })
                .block();
        assertEquals(TEST_COMPONENT_ITEM_STR, body);
    }

    @Test
    public void testCreate() {
        String body = httpClient
                .post()
                .uri("/")
                .send((httpClientRequest, nettyOutbound) -> {
                    httpClientRequest.addHeader("Content-Type", "application/json");
                    return nettyOutbound.sendString(Mono.just(TEST_ITEM_STR));
                })
                .responseSingle((httpClientResponse, byteBufMono) -> {
                    assertEquals(OK, httpClientResponse.status());
                    return byteBufMono.asString();
                })
                .block();
        assertEquals("\"idValue\"", body);
    }
}
