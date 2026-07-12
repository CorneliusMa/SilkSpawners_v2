import io.papermc.hangarpublishplugin.model.Platforms
import java.net.URI
import groovy.json.JsonSlurper

plugins {
    base
    alias(libs.plugins.hangar.publish)
    alias(libs.plugins.minotaur)
}

val pluginJar = configurations.create("pluginJar") {
    isCanBeConsumed = false
    isCanBeResolved = true
}

dependencies {
    pluginJar(project(path = ":Plugin", configuration = "shadowJarArtifact"))
}

val pluginJarFile = provider { pluginJar.singleFile }

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
    val conn = URI("https://api.modrinth.com/v2/tag/game_version").toURL().openConnection()
    conn.setRequestProperty("User-Agent", "SilkSpawners_v2-publish (github/silkspawners)")
    val body = conn.inputStream.bufferedReader().use { it.readText() }
    val tags = JsonSlurper().parseText(body) as List<Map<String, Any?>>
    return tags
        .filter { it["version_type"] == "release" }
        .map { it["version"] as String }
        .filter { mcCompare(it, minMc) >= 0 && mcCompare(it, maxMc) <= 0 }
        .sortedWith { a, b -> mcCompare(a, b) }
}

val gversion = if (System.getenv("HANGAR_PUBLISH_CHANNEL") == "Preview" ||
    System.getenv("MODRINTH_PUBLISH_CHANNEL") == "beta") {
    System.getenv("PUBLISH_BETA_VERSION")
} else {
    providers.gradleProperty("pluginVersion").get()
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
                jar.fileProvider(pluginJarFile)
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
    uploadFile.set(pluginJarFile)
    gameVersions.set(providers.provider { modrinthReleaseVersions() })
    loaders.set(listOf("paper", "spigot", "bukkit", "folia", "purpur"))
    detectLoaders.set(false)
}

tasks.matching { it.name.startsWith("publish") }.configureEach { dependsOn(pluginJar) }
tasks.named("modrinth") { dependsOn(pluginJar) }
