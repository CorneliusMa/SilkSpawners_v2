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
        val itemTags = if (atLeast(1, 16, 5)) ":versions:shared:PDC" else ":versions:shared:NBT"
        val spawnerSettings = if (atLeast(1, 12)) ":versions:shared:SpawnerSettings" else ":versions:shared:SpawnerSettingsLegacy"
        val spawnEgg = if (atLeast(1, 13)) ":versions:shared:SpawnEgg" else ":versions:shared:SpawnEggLegacy"

        project.dependencies.add("compileOnly", serverApi)
        project.dependencies.add("implementation", project.project(itemTags))
        project.dependencies.add("implementation", project.project(spawnerSettings))
        project.dependencies.add("implementation", project.project(spawnEgg))
        if (atLeast(1, 9)) project.dependencies.add("implementation", project.project(":versions:shared:DualWield"))
    }

    fun paperApi(version: String) {
        // Paper artifacts require Java 21, matching the servers the module can load on. The
        // published target is kept at 17 so the Java 17 plugin can bundle the module - its
        // classes are only ever loaded behind the version gate, on servers running Java 21
        project.extensions
            .getByType(JavaPluginExtension::class.java)
            .toolchain
            .languageVersion
            .set(JavaLanguageVersion.of(21))
        listOf("apiElements", "runtimeElements").forEach { name ->
            project.configurations.named(name) {
                attributes.attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 17)
            }
        }
        project.dependencies.add("compileOnly", "io.papermc.paper:paper-api:$version-R0.1-SNAPSHOT")
    }
}

extensions.create("versionModule", VersionModuleExtension::class.java, project)
