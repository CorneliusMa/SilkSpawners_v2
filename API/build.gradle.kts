group = "de.corneliusmay.silkspawners"
version = providers.gradleProperty("apiVersion").get()

plugins {
    `java-library`
    `maven-publish`
}

if (System.getenv("JITPACK") == "true") {
    val ref = System.getenv("VERSION").orEmpty()
    check(ref == "api-$version" || ref.endsWith("-SNAPSHOT") || ref.matches(Regex("[0-9a-f]{7,40}"))) {
        "Requested ref '$ref' is not the API release tag 'api-$version', a commit hash or a snapshot"
    }
}

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
    compileOnly(providers.gradleProperty("bukkit").get())
    compileOnly("org.jetbrains:annotations:24.1.0")
}

tasks.withType<Javadoc> {
    (options as StandardJavadocDocletOptions).addStringOption("Xdoclint:none", "-quiet")
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
