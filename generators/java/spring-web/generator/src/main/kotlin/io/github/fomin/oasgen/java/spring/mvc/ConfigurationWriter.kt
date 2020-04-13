package io.github.fomin.oasgen.java.spring.mvc

import io.github.fomin.oasgen.OutputFile
import io.github.fomin.oasgen.java.getFilePath

class ConfigurationWriter {
    fun write(
            basePackage: String,
            simpleClassName: String,
            converterClassName: String
    ): OutputFile {

        val content = """
               |package $basePackage;
               |
               |import com.fasterxml.jackson.core.JsonFactory;
               |import java.util.List;
               |import org.springframework.context.annotation.Configuration;
               |import org.springframework.http.converter.HttpMessageConverter;
               |import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
               |
               |@Configuration
               |public class $simpleClassName extends WebMvcConfigurationSupport {
               |
               |    private final JsonFactory jsonFactory;
               |
               |    public $simpleClassName(JsonFactory jsonFactory) {
               |        this.jsonFactory = jsonFactory;
               |    }
               |
               |    @Override
               |    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
               |        converters.add(0, new $converterClassName(jsonFactory));
               |    }
               |
               |}
               |""".trimMargin()
        return OutputFile(getFilePath("$basePackage.$simpleClassName"), content)
    }
}
