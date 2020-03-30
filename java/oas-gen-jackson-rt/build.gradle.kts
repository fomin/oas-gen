description = "Just another OpenAPI code generator. Jackson support classes"
version = "0.0.3"

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
    api("com.fasterxml.jackson.core:jackson-core:[2.10,)")
}
