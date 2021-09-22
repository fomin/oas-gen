description = "oas-gen - java destruction-test generator"

plugins {
    id("generator-conventions")
}

dependencies {
    api(project(":generators:java:dto:generator"))
    testImplementation(project(":test-utils"))
}

tasks.test {
    inputs.dir("../expected-test/src/main")
}
