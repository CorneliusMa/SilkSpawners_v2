plugins {
    id("silkspawners.java-conventions")
    `java-library`
}

dependencies {
    api(project(":API"))
    compileOnly(libs.bukkit)
}
