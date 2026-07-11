group = "de.corneliusmay.silkspawners"

repositories {
    maven("https://jitpack.io")
}

dependencies {
    implementation(project(":API"))

    compileOnly("org.bukkit:bukkit:1.13-R0.1-SNAPSHOT")
    compileOnly("com.github.brcdev-minecraft:shopgui-api:3.2.0") {
        isTransitive = false
    }
}
