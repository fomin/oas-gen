package io.github.fomin.oasgen;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.json.async.NonBlockingJsonParser;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.NettyOutbound;
import reactor.util.Logger;
import reactor.util.Loggers;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class ByteBufConverter {
    private static final Logger logger = Loggers.getLogger(ByteBufConverter.class);
    static final boolean PARSE_LOG_ENABLED =
            Boolean.parseBoolean(System.getProperty("io.github.fomin.oasgen.parseLogEnabled", "false"));

    private final JsonFactory jsonFactory;

    public ByteBufConverter(JsonFactory jsonFactory) {
        this.jsonFactory = jsonFactory;
    }

    public <T> Mono<T> parse(Flux<ByteBuf> byteFlux, NonBlockingParser<T> parser) {
        NonBlockingJsonParser jsonParser;
        try {
            jsonParser = (NonBlockingJsonParser) jsonFactory.createNonBlockingByteArrayParser();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        byte[] buffer = new byte[8192];
        Flux<T> valueFlux = byteFlux.handle((byteBuf, synchronousSink) -> {
            boolean done = true;
            while (true) {
                int readableBytes = byteBuf.readableBytes();
                if (readableBytes == 0) {
                    break;
                }
                int bytesToRead = Math.min(readableBytes, buffer.length);
                byteBuf.readBytes(buffer, 0, bytesToRead);
                if (PARSE_LOG_ENABLED) {
                    String input = new String(buffer, 0, bytesToRead, StandardCharsets.UTF_8);
                    logger.info("Feed body content: " + input);
                }
                try {
                    jsonParser.feedInput(buffer, 0, bytesToRead);
                    done = parser.parseNext(jsonParser);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            if (done) {
//                if (jsonParser.needMoreInput()) {
//                    throw new RuntimeException("More input is required");
//                }

                ParseResult<T> parseResult = parser.build();
                if (parseResult == ParseResult.END_ARRAY || parseResult == ParseResult.NULL_VALUE) {
                    synchronousSink.error(new RuntimeException("Array and or null value is not expected"));
                } else {
                    T value = parseResult.getValue();
                    synchronousSink.next(value);
                }
            }
        });
        return valueFlux.single();
    }

    public <T> Mono<ByteBuf> write(NettyOutbound nettyOutbound, Mono<T> objectMono, Writer<T> writer) {
        return objectMono.map(t -> {
            ByteBuf byteBuf = nettyOutbound.alloc().buffer();
            try {
                OutputStream byteBufOutputStream = new ByteBufOutputStream(byteBuf);
                JsonGenerator jsonGenerator = jsonFactory.createGenerator(byteBufOutputStream);
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
