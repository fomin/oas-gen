description = "oas-gen - typescript simple generator"

plugins {
    id("generator-conventions")
}

dependencies {
    api(project(":generators:typescript:dto:generator"))
    testImplementation(project(":test-utils"))
}
