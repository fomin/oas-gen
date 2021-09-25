plugins {
    id("java-conventions")
}

dependencies {
    implementation("com.google.code.findbugs", "jsr305", JSR_305_VERSION)
    implementation(project(":generators:java:dto:runtime"))
    testImplementation(project(":generators:java:dto:test-utils"))
}

sourceSets {
    main {
        java {
            srcDir("src/simple/java")
            srcDir("src/javadoc/java")
            srcDir("src/enum/java")
            srcDir("src/map/java")
            srcDir("src/builtin/java")
            srcDir("src/recursive/java")
        }
    }
}