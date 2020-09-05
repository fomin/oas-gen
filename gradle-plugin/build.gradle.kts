description = "oas-gen - gradle plugin"

plugins {
    kotlin("jvm")
    `maven-publish`
    signing
    `java-gradle-plugin`
    id("com.gradle.plugin-publish")
}

dependencies {
    compileOnly(project(":core"))
}

pluginBundle {
    website = "https://github.com/fomin/oas-gen/"
    vcsUrl = "https://github.com/fomin/oas-gen.git"
    tags = listOf("OpenAPI", "generator")
}

gradlePlugin {
    plugins {
        create("oas-gen-gradle-plugin") {
            id = "io.github.fomin.oas-gen"
            group = "io.github.fomin.oas-gen"
            displayName = "oas-gen Gradle plugin"
            description = "Generates clients and servers from OpenAPI"
            implementationClass = "io.github.fomin.oasgen.gradle.OasGenPlugin"
        }
    }
}

val versionFileDir = file("$buildDir/generated/versionFileDir")
val versionFile = file("${rootProject.projectDir}/version.txt")

tasks {
    processResources {
        from(versionFile) { into("META-INF/oas-gen/") }
    }
}
