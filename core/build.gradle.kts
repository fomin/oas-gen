import io.github.fomin.oasgen.gradle.*

description = "oas-gen - core classes"

plugins {
    kotlin("jvm")
    `maven-publish`
    signing
}

dependencies {
    api(kotlin("stdlib-jdk8"))
    implementation("com.fasterxml.jackson.core", "jackson-databind", JACKSON_VERSION)
    implementation("com.fasterxml.jackson.dataformat", "jackson-dataformat-yaml", JACKSON_VERSION)
}

addPublications("oas-gen-core")
