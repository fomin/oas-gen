description = "oas-gen - java spring-web generator"

plugins {
    id("kotlin-publishing-conventions")
}

dependencies {
    api(project(":generators:java:dto:generator"))
    testImplementation(project(":test-utils"))
}

tasks.test {
    inputs.dir("../expected-client/src/main")
    inputs.dir("../expected-server/src/main")
}
