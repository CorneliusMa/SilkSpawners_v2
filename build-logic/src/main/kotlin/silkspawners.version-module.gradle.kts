plugins {
    id("silkspawners.core-module")
}

// Selects the support modules matching the declared API level, so version modules stay single-declaration
open class VersionModuleExtension(private val project: Project) {

    fun spigotApi(version: String) {
        val parts = version.split('.').map(String::toInt)
        fun atLeast(vararg minimum: Int) = parts.zip(minimum.toList())
            .map { (actual, expected) -> actual.compareTo(expected) }
            .firstOrNull { it != 0 }
            ?.let { it > 0 }
            ?: (parts.size >= minimum.size)

        // 1.8 predates the split spigot-api artifact
        val serverApi = if (atLeast(1, 9)) "org.spigotmc:spigot-api:$version-R0.1-SNAPSHOT"
        else "org.bukkit:bukkit:$version-R0.1-SNAPSHOT"
        val itemTags = if (atLeast(1, 16, 5)) ":PDC" else ":NBT"
        val spawnerSettings = if (atLeast(1, 12)) ":SpawnerSettings" else ":SpawnerSettingsLegacy"

        project.dependencies.add("compileOnly", serverApi)
        project.dependencies.add("implementation", project.project(itemTags))
        project.dependencies.add("implementation", project.project(spawnerSettings))
    }
}

extensions.create("versionModule", VersionModuleExtension::class.java, project)
