package de.corneliusmay.build

import java.io.File
import java.io.StringReader
import java.security.MessageDigest
import java.util.Properties
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction

abstract class LocaleSignaturesTask : DefaultTask() {

    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val locales: DirectoryProperty

    @get:Input
    abstract val skip: Property<Boolean>

    @get:Input
    @get:Optional
    abstract val tags: Property<String>

    @get:Internal
    abstract val repository: DirectoryProperty

    @get:OutputFile
    abstract val packaged: RegularFileProperty

    @TaskAction
    fun generate() {
        val output = packaged.get().asFile
        if (skip.get()) {
            logger.warn("Locale signatures skipped, reworded messages will be treated as user customizations")
            write(output, emptyList())
            return
        }

        val listing = tags.orNull
        if (listing == null) {
            logger.warn(
                "No git repository found, reworded messages will be treated as user customizations. Build from a " +
                    "clone to derive locale signatures, or pass -PskipLocaleSignatures to silence this warning."
            )
            write(output, emptyList())
            return
        }

        val releases = listing.lines().filter { it.isNotBlank() }.map { it.substringBefore(' ') }
        if (releases.isEmpty()) {
            throw GradleException(
                "No release tags found in this repository, locale signatures cannot be derived. Fetch the full " +
                    "history and tags (fetch-depth: 0 in CI), or pass -PskipLocaleSignatures to build without them."
            )
        }

        val released = linkedMapOf<String, MutableSet<String>>()
        for (tag in releases) {
            for (path in git("ls-tree", "-r", "--name-only", tag).lines()) {
                if (!path.contains("locales/messages_")) continue
                val locale = path.substringAfter("messages_").removeSuffix(".properties")
                parse(decode(gitBytes("show", "$tag:$path"))).forEach { (key, value) ->
                    released.getOrPut("$locale/$key") { mutableSetOf() }.add(hash(value))
                }
            }
        }

        val current = linkedMapOf<String, String>()
        locales.get().asFile.listFiles()
            ?.filter { it.name.startsWith("messages_") && it.name.endsWith(".properties") }
            ?.forEach { file ->
                val locale = file.name.removePrefix("messages_").removeSuffix(".properties")
                parse(decode(file.readBytes())).forEach { (key, value) -> current["$locale/$key"] = hash(value) }
            }

        val superseded = released.keys.sorted().mapNotNull { key ->
            val rest = released.getValue(key).filter { it != current[key] }.sorted()
            if (rest.isEmpty()) null else "$key = " + rest.joinToString(",")
        }
        write(output, superseded)
        logger.lifecycle("Locale signatures: ${superseded.size} entries derived from ${releases.size} releases")
    }

    private fun git(vararg arguments: String) = decode(gitBytes(*arguments))

    private fun gitBytes(vararg arguments: String): ByteArray {
        val process = ProcessBuilder(listOf("git", *arguments))
            .directory(repository.get().asFile)
            .start()
        val output = process.inputStream.use { it.readBytes() }
        val error = process.errorStream.use { it.readBytes() }.toString(Charsets.UTF_8).trim()
        if (process.waitFor() != 0)
            throw GradleException("git ${arguments.joinToString(" ")} failed: $error")
        return output
    }

    private fun decode(bytes: ByteArray) =
        runCatching { Charsets.UTF_8.newDecoder().decode(java.nio.ByteBuffer.wrap(bytes)).toString() }
            .getOrElse { bytes.toString(Charsets.ISO_8859_1) }

    private fun parse(text: String): Map<String, String> {
        val properties = Properties()
        properties.load(StringReader(text))
        return properties.stringPropertyNames().associateWith { properties.getProperty(it) }
    }

    private fun hash(value: String) = MessageDigest.getInstance("SHA-256")
        .digest(value.toByteArray(Charsets.UTF_8))
        .joinToString("") { "%02x".format(it) }
        .substring(0, 12)

    private fun write(file: File, lines: List<String>) {
        val content = if (lines.isEmpty()) "" else lines.joinToString("\n") + "\n"
        if (file.exists() && file.readText(Charsets.UTF_8) == content) return
        file.parentFile.mkdirs()
        file.writeText(content, Charsets.UTF_8)
    }
}
