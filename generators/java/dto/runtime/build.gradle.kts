import io.github.fomin.oasgen.gradle.*

description = "oas-gen - java dto runtime support classes"

plugins {
    `java-library`
    `maven-publish`
    signing
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    api("com.fasterxml.jackson.core","jackson-core", JACKSON_VERSION)
    api("com.google.code.findbugs", "jsr305", JSR_305_VERSION)
}

addPublications("oas-gen-java-dto-runtime")
