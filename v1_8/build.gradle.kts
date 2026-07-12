group = "de.corneliusmay.silkspawners"

dependencies {
    implementation(project(":SPI"))

    compileOnly(providers.gradleProperty("bukkit").get())
}