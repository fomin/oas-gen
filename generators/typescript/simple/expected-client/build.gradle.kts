import com.github.gradle.node.npm.task.NpmTask

plugins {
    id("com.github.node-gradle.node")
}

tasks {
    val build by registering(NpmTask::class) {
        dependsOn(npmInstall)
        dependsOn(":generators:typescript:simple:runtime:build")
        args.set(listOf("run-script", "build"))
    }

    val test by registering(NpmTask::class) {
        dependsOn(build)
        args.set(listOf("test"))
    }

    val clean by registering(Delete::class) {
        delete("out")
    }

}
