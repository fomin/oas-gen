import io.github.fomin.oasgen.gradle.addPublications

description = "oas-gen - typescript axios generator"

plugins {
    kotlin("jvm")
    `maven-publish`
    signing
}

dependencies {
    api(project(":core"))
    testImplementation(project(":test-utils"))
}

addPublications("oas-gen-typescript-axios-generator")
