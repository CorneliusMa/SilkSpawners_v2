plugins {
    id("silkspawners.java-conventions")
}

group = "com.example.spawners"

tasks.jar {
    destinationDirectory.set(rootProject.layout.buildDirectory.dir("libs"))
}

dependencies {
    compileOnly(libs.bukkit)

    implementation(projects.api)
}
