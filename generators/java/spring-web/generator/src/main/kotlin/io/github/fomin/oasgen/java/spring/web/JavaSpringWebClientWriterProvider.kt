package io.github.fomin.oasgen.java.spring.web

import io.github.fomin.oasgen.OpenApiWriterProvider

class JavaSpringWebClientWriterProvider : OpenApiWriterProvider {
    override val id = "java-spring-web-client"
    override fun provide(dtoNamespace: String, routesNamespace: String, converterIds: List<String>) =
            JavaSpringWebClientWriter(dtoNamespace, routesNamespace, converterIds)
}
