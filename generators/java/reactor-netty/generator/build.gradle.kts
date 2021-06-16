description = "oas-gen - java reactor-netty generator"

plugins {
    id("generator-conventions")
}

dependencies {
    api(project(":generators:java:dto:generator"))
    testImplementation(project(":test-utils"))
}

tasks.test {
    inputs.dir("../expected-client/src/main")
    inputs.dir("../expected-server/src/main")
}
