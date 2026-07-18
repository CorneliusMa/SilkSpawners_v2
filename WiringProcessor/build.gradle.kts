plugins {
    id("silkspawners.java-conventions")
}

dependencies {
    implementation(project(":Wiring"))
    implementation(libs.bukkit)

    compileOnly(libs.auto.service.annotations)
    annotationProcessor(libs.auto.service)
}
