pluginManagement {
    includeBuild("build-logic")
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "silkspawners"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        maven("https://jitpack.io") {
            content {
                includeGroupByRegex("""com\.github\..+""")
            }
        }
    }
}

include(
    "API",
    "ApiExample",
    "SPI",
    "Plugin",
    "Publication",
    "platform:PlatformBukkit",
    "platform:PlatformFolia",
    "platform:MessageBukkit",
    "hooks:HookShopGuiPlus",
    "versions:shared:DualWield",
    "versions:shared:NBT",
    "versions:shared:PDC",
    "versions:shared:SpawnEgg",
    "versions:shared:SpawnEggLegacy",
    "versions:shared:SpawnerSettings",
    "versions:shared:SpawnerSettingsLegacy",
    "versions:v1_8",
    "versions:v1_9_4",
    "versions:v1_12_2",
    "versions:v1_13_1",
    "versions:v1_16",
    "versions:v1_16_5",
    "versions:v1_20_5",
    "versions:v1_21_3"
)
