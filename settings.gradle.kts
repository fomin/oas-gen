rootProject.name = "oas-gen"

pluginManagement {
    plugins {
        id("com.github.node-gradle.node") version "2.2.4"
        id("com.gradle.plugin-publish") version "0.12.0"
    }
}

enableFeaturePreview("ONE_LOCKFILE_PER_PROJECT")

include(":cli")
include(":client-test-utils")
include(":core")
include(":generators:java:dto:expected-dto")
include(":generators:java:dto:generator")
include(":generators:java:dto:runtime")
include(":generators:java:dto:test-utils")
include(":generators:java:reactor-netty:expected-client")
include(":generators:java:reactor-netty:expected-server")
include(":generators:java:reactor-netty:generator")
include(":generators:java:reactor-netty:runtime")
include(":generators:java:spring-web:expected-client")
include(":generators:java:spring-web:expected-server")
include(":generators:java:spring-web:generator")
include(":generators:java:spring-web:runtime")
include(":generators:java:destruction-test:expected-test")
include(":generators:java:destruction-test:generator")
include(":generators:java:destruction-test:runtime")
include(":generators:java:url-utils")
//include(":generators:typescript:dto:generator")
//include(":generators:typescript:simple:expected-client")
//include(":generators:typescript:simple:generator")
//include(":generators:typescript:simple:runtime")
include(":gradle-plugin")
include(":server-test-utils")
include(":test-schemas")
include(":test-utils")
