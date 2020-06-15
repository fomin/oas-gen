import com.moowork.gradle.node.npm.NpmTask

plugins {
    id("com.github.node-gradle.node")
}

tasks {
    val build by registering(NpmTask::class) {
        dependsOn(npmInstall)
        dependsOn(":generators:typescript:simple:runtime:build")
        setArgs(listOf("run-script", "build"))
    }

    val test by registering(NpmTask::class) {
        dependsOn(build)
        setArgs(listOf("test"))
    }

    val clean by registering(Delete::class) {
        delete("out")
    }

}
