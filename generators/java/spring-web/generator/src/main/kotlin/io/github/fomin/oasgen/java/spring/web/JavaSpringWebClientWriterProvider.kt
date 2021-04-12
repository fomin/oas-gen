package io.github.fomin.oasgen.java.spring.web

import io.github.fomin.oasgen.OpenApiWriterProvider

class JavaSpringWebClientWriterProvider : OpenApiWriterProvider {
    override val id = "java-spring-web-client"
    override fun provide(namespace: String, converterIds: List<String>) =
            JavaSpringWebClientWriter(namespace, converterIds)
}
