plugins {
    id("silkspawners.nbt-module")
}

dependencies {
    implementation(project(":SpawnerSettingsLegacy"))
    compileOnly(libs.bukkit)
}
