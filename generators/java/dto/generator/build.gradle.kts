description = "oas-gen - java dto generator"

plugins {
    id("generator-conventions")
}

dependencies {
    api(project(":core"))
    testImplementation(project(":test-utils"))
}
