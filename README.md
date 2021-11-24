# oas-gen

Just another OpenAPI code generator

## How to run via command line

```shell script
java -cp <classpath> io.github.fomin.oasgen.java.MainKt \
        -b <base-directory> \
        -p <schema-path (relative to base directory)> \
        -s <schema-file> \
        -o <output-directory> \
        -n <namespace> \
        -g <generator-id> \
        -c <converter-id> ...
```

## How to use with gradle

### Configuration example

```kotlin
import io.github.fomin.oasgen.gradle.*
import io.github.fomin.oasgen.gradle.java.*

plugins {
    id("io.github.fomin.oas-gen") version "<PLUGIN_VERSION>"
}

oasGen {
    generate("id") {
        source = DirectorySource(file("src/main/openapi"))
        schemaPath = "petstore.yaml"
        generator = javaReactorNettyClient(
            namespaceConfiguration = SingleNamespace("com.example"),
            outputConfiguration = SingleOutput(java.sourceSets.main),
        )
    }
}
```
### Predefined generators

- [java](gradle-plugin/src/main/kotlin/io/github/fomin/oasgen/gradle/java/BuiltinJavaGenerators.kt)
- [typescript](gradle-plugin/src/main/kotlin/io/github/fomin/oasgen/gradle/typescript/BuiltinTypeScriptGenerators.kt)

## Links and Trademarks

OpenAPI is a trademark of The Linux Foundation
https://www.openapis.org/
https://www.linuxfoundation.org/trademark-list/
