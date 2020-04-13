import io.github.fomin.oasgen.gradle.*

description = "oas-gen - java reactor-netty generator"

plugins {
    kotlin("jvm")
    `maven-publish`
    signing
}

dependencies {
    api(project(":generators:java:dto:generator"))
    testImplementation(project(":test-utils"))
}

addPublications("oas-gen-reactor-netty-generator")
