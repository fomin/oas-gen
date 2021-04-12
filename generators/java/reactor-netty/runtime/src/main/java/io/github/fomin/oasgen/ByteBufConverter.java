package io.github.fomin.oasgen;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import reactor.core.publisher.Mono;
import reactor.netty.ByteBufMono;
import reactor.netty.NettyOutbound;
import reactor.util.Logger;
import reactor.util.Loggers;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;

public class ByteBufConverter {
    private static final Logger logger = Loggers.getLogger(ByteBufConverter.class);
    static final boolean PARSE_LOG_ENABLED =
            Boolean.parseBoolean(System.getProperty("io.github.fomin.oasgen.parseLogEnabled", "false"));

    private final ObjectMapper objectMapper;

    public ByteBufConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public <T> Mono<T> parse(ByteBufMono byteBufMono, Function<JsonNode, T> parseFunction) {
        return byteBufMono.asByteArray().map(bytes -> {
            if (PARSE_LOG_ENABLED) {
                logger.info("Feed body content: " + new String(bytes, StandardCharsets.UTF_8));
            }
            try {
                JsonNode jsonNode = objectMapper.readTree(bytes);
                return parseFunction.apply(jsonNode);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public <T> Mono<ByteBuf> write(NettyOutbound nettyOutbound, Mono<T> objectMono, ValueWriter<T> writer) {
        return objectMono.map(t -> {
            ByteBuf byteBuf = nettyOutbound.alloc().buffer();
            try {
                OutputStream byteBufOutputStream = new ByteBufOutputStream(byteBuf);
                JsonGenerator jsonGenerator = objectMapper.createGenerator(byteBufOutputStream);
                writer.write(jsonGenerator, t);
                jsonGenerator.close();
            } catch (Exception e) {
                byteBuf.release();
                throw new RuntimeException(e);
            }
            return byteBuf;
        });
    }
}
