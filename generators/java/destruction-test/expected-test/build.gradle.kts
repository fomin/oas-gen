plugins {
    id("java-conventions")
}

java.sourceSets["main"].java {
    srcDir("src/main/java-ext")
}

dependencies {
    implementation(project(":generators:java:destruction-test:runtime"))
    testImplementation(project(":client-test-utils"))
    implementation(group = "io.rest-assured", name = "rest-assured", version = "4.4.0")
}
