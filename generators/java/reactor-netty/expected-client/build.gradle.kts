plugins {
    id("java-conventions")
}

dependencies {
    implementation(project(":generators:java:reactor-netty:runtime"))
    testImplementation(project(":client-test-utils"))
}
