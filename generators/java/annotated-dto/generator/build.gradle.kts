import io.github.fomin.oasgen.gradle.*

description = "oas-gen - java annotated dto generator"

plugins {
    kotlin("jvm")
    `maven-publish`
    signing
}

dependencies {
    api(project(":core"))
}

addPublications("oas-gen-java-annotated-dto-generator")
