import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("io.ktor.plugin") version "2.2.4"
    kotlin("jvm") version "1.8.10"
    kotlin("plugin.serialization") version "1.8.10"
    application
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("dev.inmo:tgbotapi:7.0.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")
    testImplementation(
        "io.kotest:kotest-runner-junit5-jvm:5.5.5")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClass.set("MainKt")
}
ktor {
    fatJar {
        archiveFileName.set("rtb.jar")
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform() }