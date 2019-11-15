plugins {
    `java-library`
    `maven-publish`
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    api("com.fasterxml.jackson.core:jackson-core:2.+")
}

publishing {
    publications {
        register<MavenPublication>("gpr") {
            groupId = "com.github.fomin"
            artifactId = "schema-transformer-jacksonrt"
            version = "0.0.1-SNAPSHOT"
            from(components["java"])
        }
    }
}
