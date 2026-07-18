plugins {
    id("silkspawners.java-conventions")
    alias(libs.plugins.shadow)
}

version = providers.gradleProperty("pluginVersion").get()

val shadowJarArtifact = configurations.create("shadowJarArtifact") {
    isCanBeConsumed = true
    isCanBeResolved = false
}

artifacts {
    add(shadowJarArtifact.name, tasks.named("shadowJar"))
}

val nonCoreModules = setOf("Plugin", "ApiExample", "Publication", "Wiring", "WiringProcessor")
val coreModules = rootProject.subprojects
    .filter { it.name !in nonCoreModules }
    .map { it.path }

dependencies {
    compileOnly(libs.bukkit)
    annotationProcessor(project(":WiringProcessor"))

    implementation(project(":Wiring"))
    implementation(libs.bstats.bukkit)
    implementation(libs.adventure.minimessage)
    implementation(libs.adventure.serializer.legacy)

    coreModules.forEach { module ->
        implementation(project(module))
    }
}

tasks {
    assemble {
        dependsOn(shadowJar)
    }
    processResources {
        val pluginVersion = project.version.toString()
        filesMatching("plugin.yml") {
            expand(mapOf("project" to mapOf("version" to pluginVersion)))
        }
    }
    shadowJar {
        destinationDirectory.set(rootProject.layout.buildDirectory.dir("libs"))
        archiveBaseName.set("SilkSpawners_v2")
        archiveClassifier.set("")
        archiveVersion.set("")
        minimize {
            coreModules.forEach { moduleName ->
                exclude(project(moduleName))
            }
        }
        relocate("org.bstats", "de.corneliusmay.silkspawners.plugin.lib.org.bstats")
        relocate("net.kyori", "de.corneliusmay.silkspawners.plugin.lib.net.kyori")
        dependencies {
            val bukkit = libs.bukkit.get()
            exclude(dependency("${bukkit.group}:${bukkit.name}:${bukkit.version}"))
        }
    }
}
