plugins {
    java
    id("dependency-locking-conventions")
}

version = file("$rootDir/version.txt").readText().trim()

tasks.withType<Test> {
    useJUnitPlatform()
}
