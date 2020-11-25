description = "oas-gen - server test utils"

plugins {
    id("java-library-publishing-conventions")
}

dependencies {
    api("io.projectreactor.netty", "reactor-netty", REACTOR_NETTY_VERSION)
    api("org.junit.jupiter","junit-jupiter-api", JUNIT_VERSION)
    api("org.junit.jupiter","junit-jupiter-engine", JUNIT_VERSION)
    api(project(":generators:java:url-utils"))
}
