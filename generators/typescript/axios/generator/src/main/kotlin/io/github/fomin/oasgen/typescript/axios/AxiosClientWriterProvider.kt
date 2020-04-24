package io.github.fomin.oasgen.typescript.axios

import io.github.fomin.oasgen.OpenApiWriterProvider

class AxiosClientWriterProvider : OpenApiWriterProvider {
    override val id = "typescript-axios"
    override fun provide(namespace: String) = AxiosClientWriter(namespace)
}
