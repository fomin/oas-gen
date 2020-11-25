description = "oas-gen - test utils"

plugins {
    id("kotlin-publishing-conventions")
}

dependencies {
    api(kotlin("stdlib-jdk8"))
    implementation(project(":core"))
    api("org.junit.jupiter","junit-jupiter-api", JUNIT_VERSION)
    api("org.junit.jupiter","junit-jupiter-engine", JUNIT_VERSION)
    implementation("org.eclipse.jgit", "org.eclipse.jgit", JGIT_VERSION)
    implementation("ch.qos.logback", "logback-classic", LOGBACK_VERSION)
}
