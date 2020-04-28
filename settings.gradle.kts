rootProject.name = "oas-gen"

pluginManagement {
    plugins {
        kotlin("jvm") version "1.3.71"
    }
}

include(":core")
include(":cli")
include(":test-utils")
include(":client-test-utils")

include(":generators:java:url-utils")

include(":generators:java:dto:generator")
include(":generators:java:dto:runtime")

include(":generators:java:reactor-netty:generator")
include(":generators:java:reactor-netty:runtime")
include(":generators:java:reactor-netty:expected-server")
include(":generators:java:reactor-netty:expected-client")

include(":generators:java:spring-web:generator")
include(":generators:java:spring-web:expected-server")
include(":generators:java:spring-web:expected-client")

include(":generators:typescript:axios:generator")
include(":generators:typescript:axios:runtime")
include(":generators:typescript:axios:expected-client")
