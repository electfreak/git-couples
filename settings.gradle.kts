pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}

rootProject.name = "git-couples"

include("git-couples-calculate")
include("git-couples-plugin")
include("git-couples-plugin:plugin")
include("git-couples-plugin:frontend")