import net.minecraftforge.gradle.common.util.*
import org.jetbrains.kotlin.gradle.tasks.*
import java.time.*
import java.time.format.*

plugins {
    kotlin("jvm")
    id("net.minecraftforge.gradle")
    id("com.github.masterzach32.artifactory")
    kotlin("plugin.serialization")
    `maven-publish`
    signing
}

val common = projects.common.dependencyProject

minecraft {
    mappings("official", libs.versions.mappings.get())

    runs {
        val runConfig: RunConfig.() -> Unit = {
            sources = listOf(sourceSets.main.get()) + common.sourceSets["main"]
            workingDirectory(project.file("run/$name"))
            property("forge.logging.markers", "REGISTRIES")
            property("forge.logging.console.level", "DEBUG")
        }
        register("client") {
            runConfig()
            val minecraftUUID: String? by project
            if (minecraftUUID != null)
                args("--uuid", minecraftUUID)
            val minecraftUsername: String? by project
            if (minecraftUsername != null)
                args("--username", minecraftUsername)
            val minecraftAccessToken: String? by project
            if (minecraftAccessToken != null)
                args("--accessToken", minecraftAccessToken)
        }
        register("server", runConfig)
    }
}

forge {
    applyForgeMissingLibsTempfix()
    applyInvalidModuleNameFix()
}

repositories {
    maven("https://dvs1.progwml6.com/files/maven/")
    maven("https://modmaven.k-4u.nl")
}

dependencies {
    minecraft(libs.forge.minecraft)

    implementation(libs.kotlinx.coroutines.core)

    obfRuntimeOnly(libs.forge.jei)
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xopt-in=kotlin.contracts.ExperimentalContracts")
        jvmTarget = "16"
    }
}

tasks.jar {
    manifest()
}

tasks.apiJar {
    manifest()
}

signing {
    sign(publishing.publications["mod"], publishing.publications["api"])
}

fun Jar.manifest() {
    manifest {
        attributes(
            "Specification-Title" to "mcauth",
            "Specification-Vendor" to "Forge",
            "Specification-Version" to "1", // We are version 1 of ourselves
            "Implementation-Title" to project.name,
            "Implementation-Version" to project.version,
            "Implementation-Vendor" to "zachkozar",
            "Implementation-Timestamp" to LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)
        )
    }
}