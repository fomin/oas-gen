plugins {
    `kotlin-dsl`
}

apply(from = "src/main/kotlin/dependency-locking-conventions.gradle.kts")

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:+")
}
