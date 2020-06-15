import io.github.fomin.oasgen.gradle.addPublications

description = "oas-gen - typescript simple generator"

plugins {
    kotlin("jvm")
    `maven-publish`
    signing
}

dependencies {
    api(project(":generators:typescript:dto:generator"))
    testImplementation(project(":test-utils"))
}

addPublications("oas-gen-typescript-simple-generator")
