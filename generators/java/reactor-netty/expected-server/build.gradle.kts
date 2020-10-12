plugins {
    java
}

dependencies {
    implementation(project(":generators:java:reactor-netty:runtime"))
    testImplementation(project(":server-test-utils"))
    testImplementation(project(":client-test-utils"))
}
