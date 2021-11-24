@file:JvmName("BuiltinJavaGenerators")

package io.github.fomin.oasgen.gradle.java

import io.github.fomin.oasgen.gradle.oasGenVersion

fun javaReactorNettyClient(
    namespaceConfiguration: NamespaceConfiguration,
    outputConfiguration: OutputConfiguration,
    converterIds: List<String> = emptyList(),
) = JavaGenerator(
    generatorId = "java-reactor-netty-client",
    generatorDependencies = listOf("io.github.fomin.oas-gen:oas-gen-java-reactor-netty-generator:$oasGenVersion"),
    namespaceConfiguration = namespaceConfiguration,
    outputConfiguration = outputConfiguration,
    converterIds = converterIds,
    apiDependencies = listOf("io.github.fomin.oas-gen:oas-gen-java-reactor-netty-runtime:$oasGenVersion")
)

fun javaReactorNettyServer(
    namespaceConfiguration: NamespaceConfiguration,
    outputConfiguration: OutputConfiguration,
    converterIds: List<String> = emptyList(),
) = JavaGenerator(
    generatorId = "java-reactor-netty-server",
    generatorDependencies = listOf("io.github.fomin.oas-gen:oas-gen-java-reactor-netty-generator:$oasGenVersion"),
    namespaceConfiguration = namespaceConfiguration,
    outputConfiguration = outputConfiguration,
    converterIds = converterIds,
    apiDependencies = listOf("io.github.fomin.oas-gen:oas-gen-java-reactor-netty-runtime:$oasGenVersion")
)

fun javaSpringWebClient(
    namespaceConfiguration: NamespaceConfiguration,
    outputConfiguration: OutputConfiguration,
    converterIds: List<String> = emptyList(),
) = JavaGenerator(
    generatorId = "java-spring-web-client",
    generatorDependencies = listOf("io.github.fomin.oas-gen:oas-gen-java-spring-web-generator:$oasGenVersion"),
    namespaceConfiguration = namespaceConfiguration,
    outputConfiguration = outputConfiguration,
    converterIds = converterIds,
    apiDependencies = listOf("io.github.fomin.oas-gen:oas-gen-java-spring-web-runtime:$oasGenVersion")
)

fun javaSpringWebServer(
    namespaceConfiguration: NamespaceConfiguration,
    outputConfiguration: OutputConfiguration,
    converterIds: List<String> = emptyList(),
) = JavaGenerator(
    generatorId = "java-spring-mvc",
    generatorDependencies = listOf("io.github.fomin.oas-gen:oas-gen-java-spring-web-generator:$oasGenVersion"),
    namespaceConfiguration = namespaceConfiguration,
    outputConfiguration = outputConfiguration,
    converterIds = converterIds,
    apiDependencies = listOf("io.github.fomin.oas-gen:oas-gen-java-spring-web-runtime:$oasGenVersion")
)
