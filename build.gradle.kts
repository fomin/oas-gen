import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

version = "0.0.1"
description = "Just another OpenAPI code generator"

plugins {
    kotlin("jvm") version "1.3.50"
    `maven-publish`
    signing
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
    implementation("com.fasterxml.jackson.core:jackson-databind:[2.10,)")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:[2.10,)")
    implementation("commons-cli:commons-cli:[1.4,)")
    testImplementation("org.junit.jupiter:junit-jupiter-api:[5.6,)")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:[5.6,)")
    testImplementation("org.junit.jupiter:junit-jupiter-params:[5.6,)")
    testImplementation("org.eclipse.jgit:org.eclipse.jgit:[5.7,)")
    testImplementation("ch.qos.logback:logback-classic:[1.2,)")
}

tasks.withType<Test> {
    useJUnitPlatform()
    systemProperty("testCasesDir", "$projectDir/src/test/test-cases")
}

allprojects {
    afterEvaluate {
        plugins.withType(MavenPublishPlugin::class.java) {
            publishing {
                repositories {
                    maven {
                        name = "OSSRH"
                        val releasesRepoUrl = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2")
                        val snapshotsRepoUrl = uri("https://oss.sonatype.org/content/repositories/snapshots")
                        url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl
                        credentials {
                            username = project.findProperty("ossrhUsername") as String?
                            password = project.findProperty("ossrhPassword") as String?
                        }
                    }
                }
                publications {
                    register<MavenPublication>("java") {
                        groupId = "io.github.fomin"
                        artifactId = project.name
                        from(components["java"])
                        pom {
                            name.set(project.name)
                            description.set(project.description)
                            url.set("https://github.com/fomin/oas-gen")
                            licenses {
                                license {
                                    name.set("The Apache License, Version 2.0")
                                    url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                                }
                            }
                            developers {
                                developer {
                                    id.set("andrey.n.fomin")
                                    name.set("Andrey Fomin")
                                    email.set("andrey.n.fomin@gmail.com")
                                }
                            }
                            scm {
                                connection.set("scm:git:https://github.com/fomin/oas-gen.git")
                                developerConnection.set("scm:git:https://github.com/fomin/oas-gen.git")
                                url.set("https://github.com/fomin/oas-gen")
                            }
                        }
                    }
                }
            }
        }
        plugins.withType(JavaPlugin::class) {
            java {
                withJavadocJar()
                withSourcesJar()
            }
        }
        plugins.withType(SigningPlugin::class) {
            signing {
                sign(publishing.publications["java"])
            }
        }
    }
}
