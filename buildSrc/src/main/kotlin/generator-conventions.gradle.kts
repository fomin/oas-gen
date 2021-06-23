import org.gradle.kotlin.dsl.*

plugins {
    id("kotlin-publishing-conventions")
}

val copyTestSchemas by tasks.registering {
    doLast {
        val outputDir = file("$buildDir/test-schemas")
        if (outputDir.exists()) outputDir.deleteRecursively()
        copy {
            from("$rootDir/test-schemas/src/main/resources")
            into(outputDir)
        }
    }
}

tasks.test {
    dependsOn(copyTestSchemas)
}
