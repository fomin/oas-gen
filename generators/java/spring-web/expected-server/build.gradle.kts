plugins {
    id("java-conventions")
}

dependencies {
    implementation(project(":generators:java:spring-web:runtime"))
    implementation("org.springframework", "spring-context", SPRING_WEB_VERSION)
    testImplementation(project(":server-test-utils"))
    testImplementation("org.springframework.boot", "spring-boot-starter-web", "[2.3,)")
}
