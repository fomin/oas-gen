plugins {
    java
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    implementation(project(":java:oas-gen-reactor-netty-rt"))
}

sourceSets {
    main {
        java {
            srcDir("src/expected/java")
        }
    }
}
