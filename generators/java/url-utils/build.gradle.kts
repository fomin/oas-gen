import io.github.fomin.oasgen.gradle.*

description = "oas-gen - java url utils"

plugins {
    `java-library`
    `maven-publish`
    signing
}

addPublications("oas-gen-java-url-utils")
