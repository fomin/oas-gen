description = "oas-gen - java reactor-netty runtime support classes"

plugins {
    id("java-library-publishing-conventions")
}

dependencies {
    api(project(":generators:java:dto:runtime"))
    api(project(":generators:java:url-utils"))
    api("io.projectreactor.netty", "reactor-netty", REACTOR_NETTY_VERSION)
}
