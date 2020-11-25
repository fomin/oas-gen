description = "oas-gen - java dto runtime support classes"

plugins {
    id("java-library-publishing-conventions")
}

dependencies {
    api("com.fasterxml.jackson.core","jackson-core", JACKSON_VERSION)
    api("com.google.code.findbugs", "jsr305", JSR_305_VERSION)
    testImplementation("org.junit.jupiter","junit-jupiter-api", JUNIT_VERSION)
    testImplementation("org.junit.jupiter","junit-jupiter-engine", JUNIT_VERSION)
}
