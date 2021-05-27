package io.github.fomin.oasgen.java.reactor.netty

import io.github.fomin.oasgen.OpenApiWriterProvider

class ReactorNettyClientWriterProvider : OpenApiWriterProvider {
    override val id = "java-reactor-netty-client"
    override fun provide(namespace: String, converterIds: List<String>, dtoBaseClass: String?, dtoBaseInterface: String?) =
        ReactorNettyClientWriter(namespace, namespace, converterIds, dtoBaseClass, dtoBaseInterface)

    override fun provide(
        dtoNamespace: String,
        routesNamespace: String,
        converterIds: List<String>,
        dtoBaseClass: String?,
        dtoBaseInterface: String?
    ) = ReactorNettyClientWriter(dtoNamespace, routesNamespace, converterIds, dtoBaseClass, dtoBaseInterface)
}
