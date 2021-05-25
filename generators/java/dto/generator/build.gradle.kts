import org.gradle.kotlin.dsl.support.unzipTo

description = "oas-gen - java dto generator"

plugins {
    id("kotlin-publishing-conventions")
}

val testSchemas = configurations.create("testSchemas")

dependencies {
    api(project(":core"))
    testImplementation(project(":test-utils"))
    testSchemas(project(":test-schemas"))
}

val unzipTestSchemas by tasks.registering {
    dependsOn(":test-schemas:jar")
    doLast {
        unzipTo(file("$buildDir/test-schemas"), testSchemas.singleFile)
    }
}

tasks.test {
    dependsOn(unzipTestSchemas)
}
