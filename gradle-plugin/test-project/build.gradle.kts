plugins {
    id("io.github.fomin.oas-gen") version "0.0.18-SNAPSHOT"
    java
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    oasGen("io.github.fomin.oas-gen", "oas-gen-reactor-netty-generator", "0.0.18-SNAPSHOT")
}

oasGen {
    generate(
            generatorId = "java-reactor-netty-client",
            baseDir = file("../../simple-schema"),
            schemaPath = "simple.yaml",
            namespace = "com.example",
            javaSources = true
    )
}
