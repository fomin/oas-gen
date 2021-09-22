description = "oas-gen - command line interface"

plugins {
    id("kotlin-publishing-conventions")
}

dependencies {
    implementation("commons-cli:commons-cli:[1.4,)")
    implementation(project(":generators:java:reactor-netty:generator"))
    implementation(project(":generators:java:spring-web:generator"))
//    implementation(project(":generators:typescript:simple:generator"))
}
