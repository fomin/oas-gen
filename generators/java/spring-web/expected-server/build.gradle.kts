import io.github.fomin.oasgen.gradle.*

plugins {
    java
}

dependencies {
    implementation("org.springframework", "spring-web", SPRING_WEB_VERSION)
    implementation("org.springframework", "spring-context", SPRING_WEB_VERSION)
    implementation("com.fasterxml.jackson.core","jackson-annotations", JACKSON_VERSION)
    implementation("com.google.code.findbugs", "jsr305", JSR_305_VERSION)
    testImplementation(project(":server-test-utils"))
    testImplementation("org.springframework.boot", "spring-boot-starter-web", "[2.3,)")
}
