plugins {
    kotlin("jvm") version "1.9.23"
    kotlin("plugin.serialization") version "1.9.22"
    application
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val ktor_version: String by project

dependencies {
    testImplementation(kotlin("test"))
    implementation("io.ktor:ktor-client-core:$ktor_version")
    implementation("io.ktor:ktor-client-cio:$ktor_version")

//    implementation("org.slf4j:slf4j-simple:1.6.1")
    implementation ("ch.qos.logback:logback-classic:1.4.14") // https://youtrack.jetbrains.com/issue/KTOR-1632/Failed-to-load-class-org.slf4j.impl.StaticLoggerBinder-when-SLF4J-binding-not-found-in-class-path

    implementation("org.eclipse.jgit:org.eclipse.jgit:6.9.0.202403050737-r") // jgit

    implementation("io.ktor:ktor-client-content-negotiation:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")
    implementation("io.ktor:ktor-client-auth:$ktor_version")
}

configurations {
    compileClasspath {
        extendsFrom()
    }
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

application {
    mainClass = "MainKt"
}
