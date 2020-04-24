package io.github.fomin.oasgen.java.reactor.netty

import io.github.fomin.oasgen.OpenApiWriterProvider

class ReactorNettyClientWriterProvider : OpenApiWriterProvider {
    override val id = "java-reactor-netty-client"
    override fun provide(namespace: String) = ReactorNettyClientWriter(namespace)
}
