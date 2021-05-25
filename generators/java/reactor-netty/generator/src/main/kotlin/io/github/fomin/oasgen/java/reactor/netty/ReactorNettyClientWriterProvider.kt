package io.github.fomin.oasgen.java.reactor.netty

import io.github.fomin.oasgen.OpenApiWriterProvider

class ReactorNettyClientWriterProvider : OpenApiWriterProvider {
    override val id = "java-reactor-netty-client"

    override fun provide(
        dtoNamespace: String,
        routesNamespace: String,
        converterIds: List<String>
    ) = ReactorNettyClientWriter(dtoNamespace, routesNamespace, converterIds)
}
