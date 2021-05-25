package io.github.fomin.oasgen.java.reactor.netty

import io.github.fomin.oasgen.OpenApiWriterProvider

class ReactorNettyServerWriterProvider : OpenApiWriterProvider {
    override val id = "java-reactor-netty-server"

    override fun provide(
        dtoNamespace: String,
        routesNamespace: String,
        converterIds: List<String>
    ) = ReactorNettyServerWriter(dtoNamespace, routesNamespace, converterIds)
}
