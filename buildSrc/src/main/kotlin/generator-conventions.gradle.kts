import org.gradle.kotlin.dsl.*
import org.gradle.kotlin.dsl.support.unzipTo

plugins {
    id("kotlin-publishing-conventions")
}

val testSchemas = configurations.create("testSchemas")

dependencies {
    testSchemas(project(":test-schemas"))
}

val unzipTestSchemas by tasks.registering {
    dependsOn(":test-schemas:jar")
    val outputDir = "$buildDir/test-schemas"
    outputs.dir(outputDir)
    doLast {
        unzipTo(file(outputDir), testSchemas.singleFile)
    }
}

tasks.test {
    dependsOn(unzipTestSchemas)
}
