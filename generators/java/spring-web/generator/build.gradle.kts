import io.github.fomin.oasgen.gradle.addPublications

description = "oas-gen - java spring-web generator"

plugins {
    kotlin("jvm")
    `maven-publish`
    signing
}

dependencies {
    api(project(":generators:java:annotated-dto:generator"))
    testImplementation(project(":test-utils"))
}

tasks.test {
    inputs.dir("../expected-client/src/main")
    inputs.dir("../expected-server/src/main")
}

addPublications("oas-gen-spring-web-generator")
