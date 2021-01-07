plugins {
    java
    id("dependency-locking-conventions")
}

version = file("$rootDir/version.txt").readText().trim()

tasks.withType<Test> {
    inputs.dir("$rootDir/simple-schema")
    useJUnitPlatform()
}
