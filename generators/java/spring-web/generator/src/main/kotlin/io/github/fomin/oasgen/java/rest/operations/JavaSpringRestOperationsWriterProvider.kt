package io.github.fomin.oasgen.java.rest.operations

import io.github.fomin.oasgen.OpenApiWriterProvider

class JavaSpringRestOperationsWriterProvider : OpenApiWriterProvider {
    override val id = "java-spring-rest-operations"
    override fun provide(namespace: String, converterIds: List<String>) =
            JavaSpringRestOperationsWriter(namespace, converterIds)
}
