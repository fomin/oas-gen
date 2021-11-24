import io.github.fomin.oasgen.gradle.*
import io.github.fomin.oasgen.gradle.java.*
import io.github.fomin.oasgen.gradle.typescript.*

plugins {
    id("io.github.fomin.oas-gen") version "0.1.14-SNAPSHOT"
    java
    kotlin("jvm") version "1.5.31"
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencyLocking {
    lockAllConfigurations()
    lockMode.set(LockMode.STRICT)
}

val oasGenVersion = "0.1.14-SNAPSHOT"

oasGen {
    generate("test1") {
        source = DirectorySource(file("../../../test-schemas/src/main/resources/openapi"))
        schemaPath = "simple.yaml"
        generator = javaReactorNettyClient(
            namespaceConfiguration = SingleNamespace("namespace"),
            outputConfiguration = SingleOutput(java.sourceSets.main),
        )
    }
    generate("test2") {
        source = DirectorySource(file("../../../test-schemas/src/main/resources/openapi"))
        schemaPath = "simple.yaml"
        generator = typescriptSimpleGenerator(
            outputDir = file("$buildDir/oas-gen/test2"),
        )
    }
    generate("test3") {
        source = DependencySource("io.github.fomin.oas-gen:oas-gen-test-schemas:$oasGenVersion")
        schemaPath = "openapi/simple.yaml"
        generator = JavaGenerator(
            generatorId = "java-reactor-netty-client",
            namespaceConfiguration = SingleNamespace("namespace"),
            outputConfiguration = SingleOutput(java.sourceSets.main),
            generatorDependencies = listOf("io.github.fomin.oas-gen:oas-gen-java-reactor-netty-generator:$oasGenVersion"),
            apiDependencies = listOf("io.github.fomin.oas-gen:oas-gen-java-reactor-netty-runtime:$oasGenVersion"),
        )
    }
}

val oasGen3GeneratorClasspath = configurations.create("oasGen3Classpath")
val oasGen3Source = configurations.create("oasGen3Source")

dependencies {
    oasGen3GeneratorClasspath("io.github.fomin.oas-gen", "oas-gen-java-reactor-netty-generator", oasGenVersion)
    oasGen3Source(files("../../../test-schemas/src/main/resources"))
}

val oasGen3 by tasks.registering(OasGenTask::class) {
    generatorClasspathProvider.from(oasGen3GeneratorClasspath)
    generatorId.set("java-reactor-netty-client")
    sourceDependency.from(oasGen3Source)
    basePathInSource.set("openapi")
    schemaPath.set("simple.yaml")
    dtoNamespace.set("com.example1")
    routeNamespace.set("com.example1")
    dtoOutputDir.set(file("$buildDir/oas-gen/generated3"))
    routeOutputDir.set(file("$buildDir/oas-gen/generated3"))
}
