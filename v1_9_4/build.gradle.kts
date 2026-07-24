plugins {
    id("silkspawners.nbt-module")
}

dependencies {
    implementation(project(":SpawnerSettingsLegacy"))
    compileOnly("org.spigotmc:spigot-api:1.9.4-R0.1-SNAPSHOT")
}
