group = "de.corneliusmay.silkspawners"
version = "2.3.3"

plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

var bukkit = "org.bukkit:bukkit:1.15.2-R0.1-SNAPSHOT"

dependencies {
    compileOnly(bukkit)

    implementation("org.bstats:bstats-bukkit:3.0.0")

    implementation(project(":API"))
    implementation(project(":PlatformBukkit"))
    implementation(project(":PlatformFolia"))
    implementation(project(":v1_8_4"))
    implementation(project(":v1_9_4"))
    implementation(project(":v1_12_0"))
    implementation(project(":v1_13_1"))
    implementation(project(":v1_20_5"))
    implementation(project(":v1_21_11"))
}

tasks {
    assemble {
        dependsOn(shadowJar)
    }
    processResources {
        filesMatching("plugin.yml") {
            expand(project.properties)
        }
    }
    shadowJar {
        dependsOn(processResources)
        destinationDirectory.set(rootProject.layout.buildDirectory.dir("libs"))
        archiveBaseName.set("SilkSpawners_v2")
        archiveClassifier.set("")
        archiveVersion.set("")
        relocate("org.bstats", "de.corneliusmay.silkspawners.plugin")
        dependencies {
            exclude(dependency(bukkit))
        }
    }
}