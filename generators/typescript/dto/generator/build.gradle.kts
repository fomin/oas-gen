import io.github.fomin.oasgen.gradle.addPublications

description = "oas-gen - typescript dto generator"

plugins {
    kotlin("jvm")
    `maven-publish`
    signing
}

dependencies {
    api(project(":core"))
}

addPublications("oas-gen-typescript-dto-generator")
