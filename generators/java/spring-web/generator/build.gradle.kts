import io.github.fomin.oasgen.gradle.addPublications

description = "oas-gen - java spring-web generator"

plugins {
    kotlin("jvm")
    `maven-publish`
    signing
}

dependencies {
    api(project(":generators:java:dto:generator"))
    testImplementation(project(":test-utils"))
}

addPublications("oas-gen-spring-web-generator")
