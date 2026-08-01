import de.corneliusmay.build.LocaleSignaturesTask
import de.corneliusmay.build.ReleaseTags

plugins {
    java
}

val skipSignatures = providers.gradleProperty("skipLocaleSignatures")
    .map { it.isEmpty() || it.toBoolean() }
    .orElse(false)

val releaseTags = skipSignatures.flatMap { skip ->
    if (skip) providers.provider { "" }
    else providers.of(ReleaseTags::class) { parameters.repository.set(rootDir) }
}

val localeSignatures = tasks.register<LocaleSignaturesTask>("localeSignatures") {
    locales.set(layout.projectDirectory.dir("src/main/resources/locales"))
    repository.set(project.rootDir)
    tags.set(releaseTags)
    skip.set(skipSignatures)
    packaged.set(layout.buildDirectory.file("generated/locale-signatures/signatures.properties"))
}

tasks.named<ProcessResources>("processResources") {
    from(localeSignatures.map { it.packaged }) {
        into("locale-signatures")
    }
}
