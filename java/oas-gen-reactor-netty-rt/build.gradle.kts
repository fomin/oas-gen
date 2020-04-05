description = "Just another OpenAPI code generator. Reactor-netty support classes"

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
    api(project(":java:oas-gen-jackson-rt"))
    api("io.projectreactor.netty:reactor-netty:[0.9,)")
}
