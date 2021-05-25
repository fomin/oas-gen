description = "oas-gen - java dto test utils"

plugins {
    id("java-library-publishing-conventions")
}

dependencies {
    api("org.junit.jupiter","junit-jupiter-api", JUNIT_VERSION)
    api("org.junit.jupiter","junit-jupiter-engine", JUNIT_VERSION)
    api(project(":generators:java:dto:runtime"))
}
