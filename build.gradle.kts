import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

description = "oas-gen - just another OpenAPI code generator"

plugins {
    kotlin("jvm")
    `maven-publish`
    signing
}

allprojects {
    version = file("$rootDir/version.txt").readText().trim()

    repositories {
        jcenter()
    }

    dependencyLocking {
        lockAllConfigurations()
    }

    tasks.register("resolveAndLockAll") {
        doFirst {
            require(gradle.startParameter.isWriteDependencyLocks)
        }
        doLast {
            configurations.filter {
                // Add any custom filtering on the configurations to be resolved
                it.isCanBeResolved
            }.forEach { it.resolve() }
        }
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
        targetCompatibility = "1.8"
        sourceCompatibility = "1.8"
    }

    plugins.withType(JavaPlugin::class) {
        java {
            withJavadocJar()
            withSourcesJar()
            sourceCompatibility = JavaVersion.VERSION_1_8
            targetCompatibility = JavaVersion.VERSION_1_8
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }

}
