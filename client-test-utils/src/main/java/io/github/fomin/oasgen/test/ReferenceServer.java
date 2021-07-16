package io.github.fomin.oasgen.test;

import io.github.fomin.oasgen.UrlEncoderUtils;
import reactor.core.publisher.Mono;
import reactor.netty.DisposableServer;
import reactor.netty.http.server.HttpServer;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ReferenceServer {
    public static final String BASE_PATH = "/base";
    public static final String DTO_JSON = "{\"property1\":\"value1\"}";
    public static final String POST_RESPONSE_VALUE_JSON = "\"postResponseValue\"";
    public static final String POST_RESPONSE_VALUE_STR = "postResponseValue";
    public static final byte[] TEST_BYTES = new byte[]{1, 2, 3, 100, 101, 102};

    public static DisposableServer create(int port) {
        HttpServer httpServer = HttpServer.create().host("127.0.0.1").port(port).route(httpServerRoutes ->
                httpServerRoutes
                        .post(BASE_PATH + "/path1", (request, response) -> {
                            assertEquals("application/json", request.requestHeaders().get("Content-Type"));
                            Mono<String> requestMono = request.receive().aggregate().asString();
                            Mono<String> responseMono = requestMono.map(requestBodyString -> {
                                assertEquals(DTO_JSON, requestBodyString);
                                return POST_RESPONSE_VALUE_JSON;
                            });
                            return response
                                    .header("Content-Type", "application/json")
                                    .sendString(responseMono);
                        })
                        .get(BASE_PATH + "/path2/{id}", (request, response) -> {
                            assertEquals("idValue", request.param("id"));
                            assertEquals("2021-06-01", request.requestHeaders().get("param3"));
                            Map<String, String> queryParams = UrlEncoderUtils.parseQueryParams(request.uri());
                            assertEquals("param1Value", queryParams.get("param1"));
                            assertEquals("value1", queryParams.get("param2"));
                            return response
                                    .header("Content-Type", "application/json")
                                    .sendString(Mono.just(DTO_JSON));
                        })
                        .post(BASE_PATH + "/path3", (request, response) -> {
                            assertTrue(request.uri().endsWith("?param1"));
                            return response.send();
                        })
                        .get(BASE_PATH + "/path4", (request, response) -> {
                            response.header("Content-Type", "application/octet-stream");
                            return response.sendByteArray(request.receive().then(Mono.just(TEST_BYTES)));
                        })
                        .post(BASE_PATH + "/path5", (request, response) -> {
                            assertEquals("application/octet-stream", request.requestHeaders().get("Content-Type"));
                            Mono<byte[]> requestBodyMono = request.receive().aggregate().asByteArray().doOnNext(bytes ->
                                    assertArrayEquals(TEST_BYTES, bytes)
                            );
                            return response.send(requestBodyMono.then(Mono.empty()));
                        })
        );
        return httpServer.bindNow();
    }

    private ReferenceServer() {
    }
}
