plugins {
    id("silkspawners.java-conventions")
    `java-library`
    `maven-publish`
}

version = providers.gradleProperty("apiVersion").get()

if (System.getenv("JITPACK") == "true") {
    val ref = System.getenv("VERSION").orEmpty()
    check(ref == "api-$version" || ref.endsWith("-SNAPSHOT") || ref.matches(Regex("[0-9a-f]{7,40}"))) {
        "Requested ref '$ref' is not the API release tag 'api-$version', a commit hash or a snapshot"
    }
}

java {
    withSourcesJar()
    withJavadocJar()
}

tasks.jar {
    destinationDirectory.set(rootProject.layout.buildDirectory.dir("libs"))
    archiveBaseName.set("SilkSpawners_v2-api")
    manifest {
        attributes(
            "Implementation-Title" to "SilkSpawners API",
            "Implementation-Version" to project.version,
            "SilkSpawners-Plugin-Version" to providers.gradleProperty("pluginVersion").get()
        )
    }
}

configurations.configureEach {
    exclude(group = "org.projectlombok")
}

dependencies {
    compileOnly(libs.bukkit)
    compileOnly(libs.jetbrains.annotations)
}

tasks.withType<Javadoc> {
    with(options as StandardJavadocDocletOptions) {
        docTitle("SilkSpawners API $version")
        windowTitle("SilkSpawners API $version")
        overview("src/main/javadoc/overview.html")
        links(
            "https://docs.oracle.com/en/java/javase/17/docs/api/",
            "https://hub.spigotmc.org/javadocs/spigot/",
            "https://javadoc.io/doc/org.jetbrains/annotations/${libs.jetbrains.annotations.get().version}/"
        )
        bottom("""<a href="https://github.com/CorneliusMa/SilkSpawners_v2">SilkSpawners</a> is licensed under the <a href="https://github.com/CorneliusMa/SilkSpawners_v2/blob/master/LICENSE">MIT License</a>.""")
        addStringOption("Xdoclint:none", "-quiet")
    }
}

publishing {
    publications {
        create<MavenPublication>("api") {
            artifactId = "silkspawners-api"
            from(components["java"])
            pom {
                name.set("SilkSpawners API")
                description.set("Developer API for the SilkSpawners plugin")
                url.set("https://github.com/CorneliusMa/SilkSpawners_v2")
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://github.com/CorneliusMa/SilkSpawners_v2/blob/master/LICENSE")
                    }
                }
            }
        }
    }
}
