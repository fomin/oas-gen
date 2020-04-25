import io.github.fomin.oasgen.gradle.*

description = "oas-gen - test utils"

plugins {
    kotlin("jvm")
    `maven-publish`
    signing
}

dependencies {
    api(kotlin("stdlib-jdk8"))
    implementation(project(":core"))
    api("org.junit.jupiter","junit-jupiter-api", JUNIT_VERSION)
    api("org.junit.jupiter","junit-jupiter-engine", JUNIT_VERSION)
    implementation("org.eclipse.jgit", "org.eclipse.jgit", JGIT_VERSION)
    implementation("ch.qos.logback", "logback-classic", LOGBACK_VERSION)
}

addPublications("oas-gen-test-utils")
