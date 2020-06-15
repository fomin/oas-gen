import com.moowork.gradle.node.npm.NpmTask

plugins {
    id("com.github.node-gradle.node")
}

tasks {
    val build by registering(NpmTask::class) {
        dependsOn(npmInstall)
        setArgs(listOf("run-script", "build"))
    }

    val clean by registering(Delete::class) {
        delete("out")
    }

}
