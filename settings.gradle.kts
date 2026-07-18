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
    "Wiring",
    "WiringProcessor",
    "PlatformBukkit",
    "PlatformFolia",
    "HookShopGuiPlus",
    "v1_8",
    "v1_9_4",
    "v1_12_0",
    "v1_13_1",
    "v1_20_5",
    "v1_21_3"
)
