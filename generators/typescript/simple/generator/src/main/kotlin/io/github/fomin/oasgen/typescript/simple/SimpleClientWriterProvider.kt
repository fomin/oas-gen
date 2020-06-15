package io.github.fomin.oasgen.typescript.simple

import io.github.fomin.oasgen.OpenApiWriterProvider

class SimpleClientWriterProvider : OpenApiWriterProvider {
    override val id = "typescript-simple"
    override fun provide(namespace: String, converterIds: List<String>) = SimpleClientWriter()
}
