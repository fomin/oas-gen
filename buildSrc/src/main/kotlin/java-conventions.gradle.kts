plugins {
    java
    id("base-conventions")
}

java {
    withJavadocJar()
    withSourcesJar()
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    testImplementation("org.junit.jupiter","junit-jupiter-api", JUNIT_VERSION)
    testImplementation("org.junit.jupiter","junit-jupiter-engine", JUNIT_VERSION)
}
