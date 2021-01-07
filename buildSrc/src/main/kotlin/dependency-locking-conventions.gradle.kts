repositories {
    mavenCentral()
}

dependencyLocking {
    lockAllConfigurations()
}

class VersionRejectionRule(versionPattern: String, val message: String) {
    val versionRegex = Regex(versionPattern)
}

val versionRejectionRules = listOf(
    VersionRejectionRule(".*\\b(m|M)\\d+\\b.*", "Milestone releases are rejected"),
    VersionRejectionRule(".*\\b(rc|RC)\\d+\\b.*", "Release candidates are rejected"),
    VersionRejectionRule(".*(alpha|ALPHA).*", "Alpha versions are rejected"),
    VersionRejectionRule(".*(beta|BETA).*", "Beta versions are rejected")
)

configurations.all {
    resolutionStrategy {
        componentSelection {
            all {
                versionRejectionRules.forEach { versionRejectionRule ->
                    if (candidate.version.matches(versionRejectionRule.versionRegex)) {
                        reject(versionRejectionRule.message)
                    }
                }
            }
        }
    }
}

tasks.register("resolveAndLockAll") {
    doFirst {
        require(gradle.startParameter.isWriteDependencyLocks)
    }
    doLast {
        configurations.filter {
            // Add any custom filtering on the configurations to be resolved
            it.isCanBeResolved
        }.forEach { it.resolve() }
    }
}
