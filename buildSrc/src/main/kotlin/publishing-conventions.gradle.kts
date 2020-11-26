plugins {
    `maven-publish`
    signing
}

tasks.withType<PublishToMavenRepository> {
    doFirst {
        require(!gradle.startParameter.isParallelProjectExecutionEnabled) {
            "Parallel build should be disabled"
        }
    }
}

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
        val defaultPublication = register<MavenPublication>("default") {
            groupId = "io.github.fomin.oas-gen"
            this.artifactId = "oas-gen" + project.path.replace(":", "-").replace("-generators", "")
            from(components["java"])
            versionMapping {
                this.allVariants {
                    fromResolutionResult()
                }
            }
            pom {
                name.set(project.name)
                description.set(project.provider {
                    project.description
                })
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
        signing {
            sign(defaultPublication.get())
        }
    }
}
