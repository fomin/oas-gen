package io.github.fomin.oasgen.java.spring.web

import io.github.fomin.oasgen.OpenApiWriterProvider
import io.github.fomin.oasgen.java.spring.web.JavaSpringMvcServerWriter

class JavaSpringMvcServerWriterProvider : OpenApiWriterProvider {
    override val id = "java-spring-mvc"
    override fun provide(namespace: String, converterIds: List<String>) =
            JavaSpringMvcServerWriter(namespace, converterIds)
}
