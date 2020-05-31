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

tasks.test {
    inputs.dir("../expected-client/src/main")
    inputs.dir("../expected-server/src/main")
}

addPublications("oas-gen-reactor-netty-generator")
