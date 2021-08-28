import com.spicymemes.artifactory.tasks.*
import org.jetbrains.kotlin.gradle.tasks.*

plugins {
    kotlin("jvm")
    id("fabric-loom")
    id("com.github.masterzach32.artifactory")
    kotlin("plugin.serialization")
    `maven-publish`
    signing
}

dependencies {
    minecraft(libs.fabric.minecraft)
    mappings(loom.officialMojangMappings())

    implementation(libs.kotlinx.coroutines.core)

    testImplementation(platform("org.junit:junit-bom:5.7.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

val generateModInfo by tasks.registering(GenerateKotlinModInfo::class) {
    `package`.set("mcauth.common")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xopt-in=kotlin.contracts.ExperimentalContracts", "-Xopt-in=kotlin.RequiresOptIn")
        jvmTarget = "16"
    }
}

signing {
    sign(publishing.publications["api"])
}
