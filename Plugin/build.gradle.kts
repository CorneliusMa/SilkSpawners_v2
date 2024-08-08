group = "de.corneliusmay.silkspawners"

plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

var bukkit = "org.bukkit:bukkit:1.15.2-R0.1-SNAPSHOT"

dependencies {
    compileOnly(bukkit)

    implementation("org.bstats:bstats-bukkit:3.0.0")

    implementation(project(":API"))
    implementation(project(":v1_8_R3"))
    implementation(project(":v1_9_R2"))
    implementation(project(":v1_12_R1"))
    implementation(project(":v1_13_R2"))
    implementation(project(":v1_20_R4"))
}

tasks {
    assemble {
        dependsOn(shadowJar)
    }
    shadowJar {
        destinationDirectory = rootProject.layout.buildDirectory.dir("libs")
        archiveBaseName = "SilkSpawners_v2"
        archiveClassifier = ""
        relocate("org.bstats", "de.corneliusmay.silkspawners.plugin")
        dependencies {
            exclude(dependency(bukkit))
        }
    }
}