group = "de.corneliusmay.silkspawners"
version = providers.gradleProperty("pluginVersion").get()

plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

var bukkit = providers.gradleProperty("bukkit").get()

dependencies {
    compileOnly(bukkit)

    implementation("org.bstats:bstats-bukkit:3.1.0")
    implementation("net.kyori:adventure-text-minimessage:4.26.1")
    implementation("net.kyori:adventure-text-serializer-legacy:4.26.1")

    implementation(project(":API"))
    implementation(project(":SPI"))
    implementation(project(":PlatformBukkit"))
    implementation(project(":PlatformFolia"))
    implementation(project(":HookShopGuiPlus"))
    implementation(project(":v1_8"))
    implementation(project(":v1_9_4"))
    implementation(project(":v1_12_0"))
    implementation(project(":v1_13_1"))
    implementation(project(":v1_20_5"))
    implementation(project(":v1_21_3"))
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
        relocate("org.bstats", "de.corneliusmay.silkspawners.plugin.lib.org.bstats")
        relocate("net.kyori", "de.corneliusmay.silkspawners.plugin.lib.net.kyori")
        dependencies {
            exclude(dependency(bukkit))
        }
    }
}
