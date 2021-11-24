import com.github.gradle.node.npm.task.NpmTask

plugins {
    id("com.github.node-gradle.node")
}

tasks {
    val build by registering(NpmTask::class) {
        dependsOn(npmInstall)
        args.set(listOf("run-script", "build"))
    }

    val clean by registering(Delete::class) {
        delete("out")
    }

}
