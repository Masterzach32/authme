import org.jetbrains.kotlin.gradle.tasks.*

plugins {
    kotlin("jvm")
    id("fabric-loom")
    id("com.github.masterzach32.artifactory")
    kotlin("plugin.serialization")
    `maven-publish`
    signing
}

repositories {
    maven("https://maven.shedaniel.me/")
    maven("https://maven.terraformersmc.com/releases/")
}

dependencies {
    minecraft(libs.fabric.minecraft)
    mappings(loom.officialMojangMappings())

    modImplementation(libs.bundles.fabric.implmentation)
    modImplementation(libs.fabric.modmenu)

    modImplementation(libs.fabric.clothconfig) {
        exclude(group = "net.fabricmc.fabric-api")
    }
    include(libs.fabric.clothconfig) {
        exclude(group = "net.fabricmc.fabric-api")
    }
}

java.toolchain.languageVersion.set(JavaLanguageVersion.of(16))

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xopt-in=kotlin.contracts.ExperimentalContracts")
        jvmTarget = "16"
    }
}

signing {
    sign(publishing.publications["mod"], publishing.publications["api"])
}
