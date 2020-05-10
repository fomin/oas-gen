package io.github.fomin.oasgen.java.spring.mvc

import io.github.fomin.oasgen.OutputFile
import io.github.fomin.oasgen.indentWithMargin
import io.github.fomin.oasgen.java.JavaOperation
import io.github.fomin.oasgen.java.getFilePath
import io.github.fomin.oasgen.java.dto.jackson.wstatic.ConverterRegistry

class MessageConverterWriter {
    fun write(
            basePackage: String,
            simpleClassName: String,
            converterRegistry: ConverterRegistry,
            javaOperations: List<JavaOperation>
    ): OutputFile {

        val supportedSchemas = (javaOperations.mapNotNull { javaOperation ->
            javaOperation.responseVariable.schema
        } + javaOperations.mapNotNull { javaOperation ->
            javaOperation.requestVariable?.schema
        }).toSortedSet(Comparator { o1, o2 ->
            converterRegistry[o1].valueType().compareTo(converterRegistry[o2].valueType())
        })

        val supportedTypes = supportedSchemas.joinToString(",\n") { schema ->
            "${converterRegistry[schema].valueType()}.class"
        }

        val parserAssignmentCases = supportedSchemas.mapIndexed { index, jsonSchema ->
            val type = converterRegistry[jsonSchema].valueType()
            val parserCreateExpression = converterRegistry[jsonSchema].parserCreateExpression()
            """|${if (index > 0) "else " else ""}if (clazz == $type.class)
               |    parser = $parserCreateExpression;
            """.trimMargin()
        }

        val writerCases = supportedSchemas.mapIndexed {index, jsonSchema ->
            val type = converterRegistry[jsonSchema].valueType()
            val writerCreateExpression = converterRegistry[jsonSchema].writerCreateExpression()
            """|${if (index > 0) "else " else ""}if (obj.getClass() == $type.class)
               |    $writerCreateExpression.write(jsonGenerator, ($type) obj);
            """.trimMargin()
        }

        val content = """
               |package $basePackage;
               |
               |import java.io.IOException;
               |import java.io.InputStream;
               |import java.io.OutputStream;
               |import java.util.*;
               |import com.fasterxml.jackson.core.JsonFactory;
               |import com.fasterxml.jackson.core.JsonGenerator;
               |import com.fasterxml.jackson.core.json.async.NonBlockingJsonParser;
               |import io.github.fomin.oasgen.NonBlockingParser;
               |import io.github.fomin.oasgen.ParseResult;
               |import org.springframework.http.HttpHeaders;
               |import org.springframework.http.HttpInputMessage;
               |import org.springframework.http.HttpOutputMessage;
               |import org.springframework.http.MediaType;
               |import org.springframework.http.converter.HttpMessageConverter;
               |
               |public class $simpleClassName implements HttpMessageConverter<Object> {
               |
               |    private static final Set<Class<?>> SUPPORTED_CLASSES = new HashSet<>(Arrays.asList(
               |            ${supportedTypes.indentWithMargin(3)}
               |    ));
               |
               |    private final JsonFactory jsonFactory;
               |
               |    public $simpleClassName(JsonFactory jsonFactory) {
               |        this.jsonFactory = jsonFactory;
               |    }
               |
               |    @Override
               |    public boolean canRead(Class<?> clazz, MediaType mediaType) {
               |        return mediaType != null
               |                && mediaType.isCompatibleWith(MediaType.APPLICATION_JSON)
               |                && SUPPORTED_CLASSES.contains(clazz);
               |    }
               |
               |    @Override
               |    public boolean canWrite(Class<?> clazz, MediaType mediaType) {
               |        return mediaType != null
               |                && mediaType.isCompatibleWith(MediaType.APPLICATION_JSON)
               |                && SUPPORTED_CLASSES.contains(clazz);
               |    }
               |
               |    @Override
               |    public List<MediaType> getSupportedMediaTypes() {
               |        return Collections.singletonList(MediaType.APPLICATION_JSON);
               |    }
               |
               |    @Override
               |    public Object read(Class<?> clazz, HttpInputMessage inputMessage) throws IOException {
               |        NonBlockingJsonParser jsonParser = (NonBlockingJsonParser) jsonFactory.createNonBlockingByteArrayParser();
               |        InputStream inputStream = inputMessage.getBody();
               |        NonBlockingParser<?> parser;
               |        ${parserAssignmentCases.indentWithMargin(2)}
               |        else
               |            throw new UnsupportedOperationException("Unsupported class " + clazz);
               |        byte[] buffer = new byte[8192];
               |        int read;
               |        while ((read = inputStream.read(buffer)) >= 0) {
               |            jsonParser.feedInput(buffer, 0, read);
               |            parser.parseNext(jsonParser);
               |        }
               |        ParseResult<?> parseResult = parser.build();
               |        if (parseResult == ParseResult.NULL_VALUE) {
               |            return null;
               |        } else {
               |            return parseResult.getValue();
               |        }
               |    }
               |
               |    @Override
               |    public void write(Object obj, MediaType contentType, HttpOutputMessage outputMessage) throws IOException {
               |        HttpHeaders headers = outputMessage.getHeaders();
               |        headers.add("Content-Type", "application/json");
               |        OutputStream outputStream = outputMessage.getBody();
               |        try (JsonGenerator jsonGenerator = jsonFactory.createGenerator(outputStream)) {
               |            ${writerCases.indentWithMargin(3)}
               |            else
               |                throw new UnsupportedOperationException("Unsupported class " + obj.getClass());
               |        }
               |    }
               |
               |}
               |""".trimMargin()
        return OutputFile(getFilePath("$basePackage.$simpleClassName"), content)
    }
}
