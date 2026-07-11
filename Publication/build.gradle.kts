import io.papermc.hangarpublishplugin.model.Platforms
import java.net.URL
import groovy.json.JsonSlurper

plugins {
    id("io.papermc.hangar-publish-plugin") version "0.1.4"
    id("com.modrinth.minotaur") version "2.8.7"
}

evaluationDependsOn(":Plugin")

// Waiting for https://docs.gradle.org/current/userguide/declaring_dependencies.html#sec:type-safe-project-accessors
var plugin = project(":Plugin")
// com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar ext org.gradle.api.tasks.bundling.Jar
var shadowJar = plugin.tasks.named<Jar>("shadowJar")

// Minecraft ranges
fun mcKey(v: String): List<Int> = v.split(".").map { it.toIntOrNull() ?: 0 }
fun mcCompare(a: String, b: String): Int {
    val ka = mcKey(a); val kb = mcKey(b)
    for (i in 0 until maxOf(ka.size, kb.size)) {
        val d = ka.getOrElse(i) { 0 } - kb.getOrElse(i) { 0 }
        if (d != 0) return d
    }
    return 0
}

// NOTE: every "release" game version Modrinth knows within [minMc, maxMc], oldest first.
@Suppress("UNCHECKED_CAST")
fun modrinthReleaseVersions(): List<String> {
    val (minMc, maxMc) = System.getenv("PUBLISH_VERSIONS").split("-", limit = 2)
    val conn = URL("https://api.modrinth.com/v2/tag/game_version").openConnection()
    conn.setRequestProperty("User-Agent", "SilkSpawners_v2-publish (github/silkspawners)")
    val body = conn.inputStream.bufferedReader().use { it.readText() }
    val tags = JsonSlurper().parseText(body) as List<Map<String, Any?>>
    return tags
        .filter { it["version_type"] == "release" }
        .map { it["version"] as String }
        .filter { mcCompare(it, minMc) >= 0 && mcCompare(it, maxMc) <= 0 }
        .sortedWith { a, b -> mcCompare(a, b) }
}

var gversion = plugin.version as String
if (System.getenv("HANGAR_PUBLISH_CHANNEL") == "Preview" ||
    System.getenv("MODRINTH_PUBLISH_CHANNEL") == "beta") {
    gversion = gversion + "+" + System.getenv("PUBLISH_SHORTY")
}

hangarPublish {
    publications.register("plugin") {
        apiKey.set(System.getenv("HANGAR_API_TOKEN"))
        id.set(System.getenv("HANGAR_ID"))
        channel.set(System.getenv("HANGAR_PUBLISH_CHANNEL"))
        changelog.set(System.getenv("HANGAR_PUB_CHANGELOG"))
        version.set(gversion)
        platforms {
            register(Platforms.PAPER) {
                jar.set(shadowJar.flatMap { it.archiveFile })
                platformVersions.set(listOf(System.getenv("PUBLISH_VERSIONS")))
            }
        }
    }
}

modrinth {
    token.set(System.getenv("MODRINTH_TOKEN"))
    projectId.set(System.getenv("MODRINTH_ID"))
    versionNumber.set(gversion)
    versionType.set(System.getenv("MODRINTH_PUBLISH_CHANNEL"))
    changelog.set(System.getenv("MODRINTH_CHANGELOG"))
    uploadFile.set(shadowJar.flatMap { it.archiveFile })
    gameVersions.set(providers.provider { modrinthReleaseVersions() })
    loaders.set(listOf("paper", "spigot", "bukkit", "folia", "purpur"))
    detectLoaders.set(false)
}

tasks.matching { it.name.startsWith("publish") }.all { dependsOn(shadowJar) }
tasks.named("modrinth") { dependsOn(shadowJar) }
