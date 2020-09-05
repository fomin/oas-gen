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

```kotlin
plugins {
    id("io.github.fomin.oas-gen") version "<PLUGIN_VERSION>"
}

dependencies {
    // add generators to configuration "oas-gen"
    // there is example for reactor-netty generator
    oasGen("io.github.fomin.oas-gen", "oas-gen-reactor-netty-generator", "0.0.18")
}

oasGen {
    generate(
            generatorId = "java-reactor-netty-client",
            baseDir = file("../../simple-schema"),
            schemaPath = "simple.yaml",
            namespace = "com.example",
            // create java source-set with generated files
            javaSources = true
    )
}
```

## Links and Trademarks

OpenAPI is a trademark of The Linux Foundation
https://www.openapis.org/
https://www.linuxfoundation.org/trademark-list/
