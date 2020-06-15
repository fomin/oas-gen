import io.github.fomin.oasgen.gradle.*

description = "oas-gen - command line interface"

plugins {
    kotlin("jvm")
    `maven-publish`
    signing
}

dependencies {
    implementation("commons-cli:commons-cli:[1.4,)")
    implementation(project(":generators:java:reactor-netty:generator"))
    implementation(project(":generators:java:spring-web:generator"))
    implementation(project(":generators:typescript:simple:generator"))
}

addPublications("oas-gen-cli")
