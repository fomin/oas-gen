package io.github.fomin.oasgen.java.spring.web

import io.github.fomin.oasgen.OpenApiWriterProvider

class JavaSpringMvcServerWriterProvider : OpenApiWriterProvider {
    override val id = "java-spring-mvc"

    override fun provide(
        dtoNamespace: String,
        routesNamespace: String,
        converterIds: List<String>
    ) = JavaSpringMvcServerWriter(dtoNamespace, routesNamespace, converterIds)
}
