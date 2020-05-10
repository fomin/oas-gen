package io.github.fomin.oasgen.java.spring.mvc

import io.github.fomin.oasgen.OpenApiWriterProvider

class JavaSpringMvcServerWriterProvider : OpenApiWriterProvider {
    override val id = "java-spring-mvc"
    override fun provide(namespace: String, converterIds: List<String>) =
            JavaSpringMvcServerWriter(namespace, converterIds)
}
