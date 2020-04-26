import io.github.fomin.oasgen.gradle.*

description = "oas-gen - java reactor-netty runtime support classes"

plugins {
    `java-library`
    `maven-publish`
    signing
}

dependencies {
    api(project(":generators:java:dto:runtime"))
    api(project(":generators:java:url-utils"))
    api("io.projectreactor.netty", "reactor-netty", REACTOR_NETTY_VERSION)
}

addPublications("oas-gen-reactor-netty-runtime")
