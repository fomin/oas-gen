description = "oas-gen - core classes"

plugins {
    id("kotlin-publishing-conventions")
}

dependencies {
    api(kotlin("stdlib-jdk8"))
    implementation("com.fasterxml.jackson.core", "jackson-databind", JACKSON_VERSION)
    implementation("com.fasterxml.jackson.dataformat", "jackson-dataformat-yaml", JACKSON_VERSION)
    implementation("com.atlassian.commonmark", "commonmark", COMMONMARK_VERSION)
}
