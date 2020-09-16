plugins {
    id("io.github.fomin.oas-gen") version "0.0.19-SNAPSHOT"
    java
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    oasGen("io.github.fomin.oas-gen", "oas-gen-reactor-netty-generator", "0.0.19-SNAPSHOT")
}

oasGen {
    generateFromDirectory(
            generatorId = "java-reactor-netty-client",
            baseDir = file("../../simple-schema"),
            schemaPath = "simple.yaml",
            namespace = "com.example1",
            javaSources = true
    )
    generateFromDependency(
            generatorId = "java-reactor-netty-client",
            dependency = "io.github.fomin.oas-gen:simple-schema:0.0.1",
            basePath = "simple-schema",
            schemaPath = "simple.yaml",
            namespace = "com.example2",
            javaSources = true
    )
}
