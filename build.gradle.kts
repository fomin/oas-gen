import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.50"
    `maven-publish`
}

allprojects {
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
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
    targetCompatibility = "1.8"
    sourceCompatibility = "1.8"
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("com.fasterxml.jackson.core:jackson-databind:2.+")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.+")
    implementation("commons-cli:commons-cli:1.+")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.+")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.+")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.+")
    testImplementation("org.eclipse.jgit:org.eclipse.jgit:5.+")
    testImplementation("ch.qos.logback:logback-classic:1.2.+")
}

tasks.withType<Test> {
    useJUnitPlatform()
    systemProperty("testCasesDir", "$projectDir/src/test/test-cases")
}

allprojects {
    plugins.withType(MavenPublishPlugin::class.java) {
        publishing {
            repositories {
                maven {
                    name = "GitHubPackages"
                    url = uri("https://maven.pkg.github.com/fomin/schema-transformer")
                    credentials {
                        username = project.findProperty("gpr.user") as String? ?: System.getenv("USERNAME")
                        password = project.findProperty("gpr.key") as String? ?: System.getenv("PASSWORD")
                    }
                }
            }
        }
    }
}

publishing {
    publications {
        register<MavenPublication>("gpr") {
            groupId = "com.github.fomin"
            artifactId = "schema-transformer"
            version = "0.0.1-SNAPSHOT"
            from(components["java"])
        }
    }
}
