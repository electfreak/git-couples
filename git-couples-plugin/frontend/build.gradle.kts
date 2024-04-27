import com.github.gradle.node.npm.task.NpmTask

plugins {
    id("com.github.node-gradle.node") version "7.0.2"
}

val default by configurations.creating
val nodeVersion = File("${project.projectDir}/.nvmrc").readText().drop(1).trim()

node {
    version.set(nodeVersion)
    npmVersion.set("")
    npmInstallCommand.set("install")
    distBaseUrl.set("https://nodejs.org/dist")
    download.set(true)
    workDir.set(file("${project.projectDir}/.cache/nodejs"))
    npmWorkDir.set(file("${project.projectDir}/.cache/npm"))
    nodeProjectDir.set(file("${project.projectDir}"))
}

val buildTaskUsingNpm = tasks.register<NpmTask>("npmBuild") {
    dependsOn(tasks.npmInstall)
    npmCommand.set(listOf("run", "build"))
    args.set(
        listOf(
            "--", "--out-dir", "${buildDir}/npm-output", "--base=/gitCouplesResources/frontend/" // badly hardcoded
        )
    )
    inputs.dir("src")
    inputs.file("index.html")
    outputs.dir("${buildDir}/npm-output")
}

artifacts {
    add(default.name, buildTaskUsingNpm)
}

tasks.register("clean") {
    group = "custom"
    description = "Custom clean task"
    doLast {
        listOf(
            project.projectDir.resolve("node_modules"),
            project.projectDir.resolve(".cache"),
            buildDir
        ).forEach { it.deleteRecursively() }
    }
}