plugins {
    java
}

val libs = the<VersionCatalogsExtension>().named("libs")

group = "de.corneliusmay.silkspawners"

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

tasks.withType<AbstractArchiveTask>().configureEach {
    isPreserveFileTimestamps = false
    isReproducibleFileOrder = true
}

dependencies {
    compileOnly(libs.findLibrary("lombok").get())
    annotationProcessor(libs.findLibrary("lombok").get())
}
