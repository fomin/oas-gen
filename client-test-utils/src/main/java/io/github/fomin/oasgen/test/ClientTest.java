package io.github.fomin.oasgen.test;

import java.io.IOException;

public interface ClientTest {
    void testPost();

    void testGet();

    void testNullableParameter();

    void testReturnOctetStream();

    void testSendOctetStream();
}
