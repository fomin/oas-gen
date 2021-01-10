plugins {
    id("java-conventions")
}

dependencies {
    implementation("org.junit.jupiter","junit-jupiter-api", JUNIT_VERSION)
    implementation(project(":generators:java:dto:runtime"))
}
