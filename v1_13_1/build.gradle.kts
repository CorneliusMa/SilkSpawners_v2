plugins {
    id("silkspawners.nbt-module")
}

dependencies {
    implementation(project(":SpawnerSettings"))
    compileOnly("org.spigotmc:spigot-api:1.13.2-R0.1-SNAPSHOT")
}
