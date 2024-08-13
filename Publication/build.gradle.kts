import io.papermc.hangarpublishplugin.model.Platforms

plugins {
    id("io.papermc.hangar-publish-plugin") version "0.1.2"
}

evaluationDependsOn(":Plugin")

// Waiting for https://docs.gradle.org/current/userguide/declaring_dependencies.html#sec:type-safe-project-accessors
var plugin = project(":Plugin")
// com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar ext org.gradle.api.tasks.bundling.Jar
var shadowJar = plugin.tasks.named<Jar>("shadowJar")

hangarPublish {
    publications.register("plugin") {
        apiKey.set(System.getenv("HANGAR_API_TOKEN"))
        id.set(System.getenv("HANGAR_ID"))
        channel.set(System.getenv("HANGAR_PUB_CHANNEL"))
        changelog.set(System.getenv("HANGAR_PUB_CHANGELOG"))
        version.set(plugin.version as String)
        platforms {
            register(Platforms.PAPER) {
                jar.set(shadowJar.flatMap { it.archiveFile })
                platformVersions.set(listOf(System.getenv("HANGAR_PLATFORM_VERSIONS")))
            }
        }
    }
}

// Install depend for publish*ToHangar
tasks.matching { it.name.startsWith("publish") }.all { dependsOn(shadowJar) }