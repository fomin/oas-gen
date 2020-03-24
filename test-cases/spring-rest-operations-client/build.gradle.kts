plugins {
    java
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    implementation(project(":java:oas-gen-jackson-rt"))
    implementation("org.springframework:spring-web:5.+")
    implementation("org.springframework.boot:spring-boot-starter-web:2.+")
}

sourceSets {
    main {
        java {
            srcDir("src/expected/java")
        }
    }
}
