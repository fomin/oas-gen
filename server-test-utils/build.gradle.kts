import io.github.fomin.oasgen.gradle.*

description = "oas-gen - server test utils"

plugins {
    `java-library`
    `maven-publish`
    signing
}

dependencies {
    api("io.projectreactor.netty", "reactor-netty", REACTOR_NETTY_VERSION)
    api("org.junit.jupiter","junit-jupiter-api", JUNIT_VERSION)
    api("org.junit.jupiter","junit-jupiter-engine", JUNIT_VERSION)
    api(project(":generators:java:url-utils"))
}

addPublications("oas-gen-server-test-utils")
