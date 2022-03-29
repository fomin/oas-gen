plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
}

apply(from = "src/main/kotlin/dependency-locking-conventions.gradle.kts")

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:+")
    implementation("com.github.node-gradle:gradle-node-plugin:+")
    implementation("com.gradle.publish:plugin-publish-plugin:+")
}
