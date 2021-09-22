package io.github.fomin.oasgen.java.destruction.test

import io.github.fomin.oasgen.OpenApiWriterProvider

class JavaDestructionTestWriterProvider : OpenApiWriterProvider {
    override val id = "java-destruction-test"

    override fun provide(
        dtoNamespace: String,
        routesNamespace: String,
        converterIds: List<String>
    ) = JavaDestructionTestWriter(dtoNamespace, routesNamespace, converterIds)
}
