plugins {
    id("silkspawners.pdc-module")
}

dependencies {
    implementation(project(":SpawnerSettings"))
    compileOnly("org.spigotmc:spigot-api:1.21.3-R0.1-SNAPSHOT")
}
