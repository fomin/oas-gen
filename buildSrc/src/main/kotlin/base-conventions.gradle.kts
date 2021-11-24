plugins {
    java
    id("dependency-locking-conventions")
}

version = file("$rootDir/version.txt").readText().trim()

tasks.withType<Test> {
    useJUnitPlatform()
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}
