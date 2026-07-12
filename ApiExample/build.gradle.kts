group = "com.example.spawners"

tasks.jar {
    destinationDirectory.set(rootProject.layout.buildDirectory.dir("libs"))
}

dependencies {
    compileOnly(providers.gradleProperty("bukkit").get())

    implementation(project(":API"))
}
