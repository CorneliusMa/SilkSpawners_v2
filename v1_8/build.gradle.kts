group = "de.corneliusmay.silkspawners"

dependencies {
    implementation(project(":API"))

    compileOnly(providers.gradleProperty("bukkit").get())
}