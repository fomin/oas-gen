rootProject.name = "oas-gen"

pluginManagement {
    plugins {
        kotlin("jvm") version "1.3.71"
        id("com.github.node-gradle.node") version "2.2.4"
    }
}

enableFeaturePreview("ONE_LOCKFILE_PER_PROJECT")

include(":core")
include(":cli")
include(":test-utils")
include(":client-test-utils")
include(":server-test-utils")

include(":generators:java:url-utils")

include(":generators:java:annotated-dto:generator")

include(":generators:java:dto:generator")
include(":generators:java:dto:runtime")

include(":generators:java:reactor-netty:generator")
include(":generators:java:reactor-netty:runtime")
include(":generators:java:reactor-netty:expected-server")
include(":generators:java:reactor-netty:expected-client")

include(":generators:java:spring-web:generator")
include(":generators:java:spring-web:expected-server")
include(":generators:java:spring-web:expected-client")

include(":generators:typescript:dto:generator")

include(":generators:typescript:simple:generator")
include(":generators:typescript:simple:runtime")
include(":generators:typescript:simple:expected-client")
