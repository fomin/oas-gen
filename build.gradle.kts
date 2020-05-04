import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

description = "oas-gen - just another OpenAPI code generator"

plugins {
    kotlin("jvm")
    `maven-publish`
    signing
}

class VersionRejectionRule(versionPattern: String, val message: String) {
    val versionRegex = Regex(versionPattern)
}

val versionRejectionRules = listOf(
        VersionRejectionRule(".*\\b(m|M)\\d+\\b.*", "Milestone releases are rejected"),
        VersionRejectionRule(".*\\b(rc|RC)\\d+\\b.*", "Release candidates are rejected"),
        VersionRejectionRule(".*(alpha|ALPHA).*", "Alpha versions are rejected"),
        VersionRejectionRule(".*(beta|BETA).*", "Beta versions are rejected")
)

allprojects {
    version = file("$rootDir/version.txt").readText().trim()

    repositories {
        mavenCentral()
    }

    dependencyLocking {
        lockAllConfigurations()
    }

    configurations.all {
        resolutionStrategy {
            componentSelection {
                all {
                    versionRejectionRules.forEach { versionRejectionRule ->
                        if (candidate.version.matches(versionRejectionRule.versionRegex)) {
                            reject(versionRejectionRule.message)
                        }
                    }
                }
            }
        }
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
        inputs.dir("$rootDir/simple-schema")
        useJUnitPlatform()
    }

}
