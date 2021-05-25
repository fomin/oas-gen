package io.github.fomin.oasgen

interface OpenApiWriterProvider {
    val id: String
    fun provide(namespace: String, converterIds: List<String>): Writer<OpenApiSchema>
    fun provide(dtoNamespace: String, routesNamespace: String, converterIds: List<String>): Writer<OpenApiSchema>
}
