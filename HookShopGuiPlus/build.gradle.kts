plugins {
    id("silkspawners.core-module")
}

dependencies {
    compileOnly("org.bukkit:bukkit:1.13-R0.1-SNAPSHOT")
    compileOnly("com.github.brcdev-minecraft:shopgui-api:3.2.0") {
        isTransitive = false
    }
}
