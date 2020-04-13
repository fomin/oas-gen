import io.github.fomin.oasgen.gradle.*

plugins {
    java
}

dependencies {
    implementation(project(":generators:java:dto:runtime"))
    implementation("org.springframework", "spring-webmvc", SPRING_WEB_VERSION)
}
