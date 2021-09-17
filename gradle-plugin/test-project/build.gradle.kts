plugins {
    id("io.github.fomin.oas-gen") version "0.1.13-SNAPSHOT"
    java
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencyLocking {
    lockAllConfigurations()
    lockMode.set(LockMode.STRICT)
}

val oasGenVersion = "0.1.13-SNAPSHOT"
dependencies {
    oasGen("io.github.fomin.oas-gen", "oas-gen-java-reactor-netty-generator", oasGenVersion)
    implementation("io.github.fomin.oas-gen", "oas-gen-java-reactor-netty-runtime", oasGenVersion)
}

oasGen {
    generateFromDirectory(
            generatorId = "java-reactor-netty-client",
            baseDir = file("../../test-schemas/src/main/resources/openapi"),
            schemaPath = "simple.yaml",
            namespace = "com.example1",
            javaSources = true
    )
    generateFromDependency(
            generatorId = "java-reactor-netty-client",
            dependency = "io.github.fomin.oas-gen:oas-gen-test-schemas:$oasGenVersion",
            basePath = "openapi",
            schemaPath = "simple.yaml",
            namespace = "com.example2",
            javaSources = true
    )
}
