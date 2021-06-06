package io.github.fomin.oasgen.typescript.simple

import io.github.fomin.oasgen.OpenApiWriterProvider

class SimpleClientWriterProvider : OpenApiWriterProvider {
    override val id = "typescript-simple"
    override fun provide(
        dtoNamespace: String,
        routesNamespace: String,
        converterIds: List<String>
    ) = SimpleClientWriter(converterIds)
}
