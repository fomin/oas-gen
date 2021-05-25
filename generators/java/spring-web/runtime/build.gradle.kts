description = "oas-gen - java sprint-web runtime support classes"

plugins {
    id("java-library-publishing-conventions")
}

dependencies {
    api(project(":generators:java:dto:runtime"))
    api("org.springframework", "spring-webmvc", SPRING_WEB_VERSION)
    compileOnlyApi("org.apache.tomcat.embed:tomcat-embed-core:9.0.44")
}
