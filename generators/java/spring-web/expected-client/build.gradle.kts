plugins {
    id("java-conventions")
}

dependencies {
    implementation(project(":generators:java:spring-web:runtime"))
    testImplementation(project(":client-test-utils"))
    testImplementation("org.springframework.boot", "spring-boot-starter-web", "[2.3,)")
}
