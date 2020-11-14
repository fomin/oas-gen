package io.github.fomin.oasgen.test;

import io.github.fomin.oasgen.UrlEncoderUtils;
import reactor.core.publisher.Mono;
import reactor.netty.DisposableServer;
import reactor.netty.http.server.HttpServer;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ReferenceServer {
    public static final String BASE_PATH = "/base";
    private static final String TEST_ITEM_STR = "{\"commonProperty1\":\"common property 1 value\",\"property1\":\"property 1 value\",\"property2\":{\"commonProperty1\":\"inner common property 1 value\",\"property21\":\"property 21 value\",\"property22\":\"value1\"},\"decimalProperty\":1,\"localDateTimeProperty\":\"2020-01-01T01:01:00\",\"stringArrayProperty\":[\"array value 1\",\"array value 2\"],\"dateTimeArrayProperty\":[\"2020-11-10T01:01:01+01:00\"],\"mapProperty\":{\"key 1\":10},\"dateTimeMapProperty\":{\"key 1\":\"2020-11-10T01:01:01+01:00\"},\"true\":{\"property1\":\"property 1 value\"},\"1 with space-and+other_çhars\":{\"property1\":\"property 1 value\"}}";
    public static final String TEST_COMPONENT_STR = "{}";

    public static DisposableServer create(int port) {
        HttpServer httpServer = HttpServer.create().port(port).port(port).route(httpServerRoutes ->
                httpServerRoutes
                        .post(BASE_PATH + "/", (request, response) -> {
                            Mono<String> requestMono = request.receive().aggregate().asString();
                            Mono<String> responseMono = requestMono.map(requestBodyString -> {
                                assertEquals(TEST_ITEM_STR, requestBodyString);
                                return "\"idValue\"";
                            });
                            return response
                                    .header("Content-Type", "application/json")
                                    .sendString(responseMono);
                        })
                        .post(BASE_PATH + "/post-without-request-body", (request, response) -> {
                            Mono<String> requestMono = request.receive().aggregate().asString();
                            Mono<String> responseMono = requestMono.map(requestBodyString -> {
                                assertEquals("expectedRequestBody", requestBodyString);
                                return "responseBodyString";
                            });
                            return response
                                    .header("Content-Type", "application/json")
                                    .sendString(responseMono);
                        })
                        .get(BASE_PATH + "/find", (request, response) -> {
                            Map<String, String> queryParams = UrlEncoderUtils.parseQueryParams(request.uri());
                            assertEquals("param1Value", queryParams.get("param1"));
                            assertEquals("value2", queryParams.get("param2"));
                            return response
                                    .header("Content-Type", "application/json")
                                    .sendString(Mono.just(TEST_ITEM_STR));
                        })
                        .get(BASE_PATH + "/{id}", (request, response) -> {
                            String param0 = request.param("id");
                            assertEquals("idValue", param0);
                            return response
                                    .header("Content-Type", "application/json")
                                    .sendString(Mono.just(TEST_COMPONENT_STR));
                        }));
        return httpServer.bindNow();
    }

    private ReferenceServer() {
    }
}
