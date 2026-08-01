package de.corneliusmay.build

import java.io.IOException
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.ValueSource
import org.gradle.api.provider.ValueSourceParameters

abstract class ReleaseTags : ValueSource<String, ReleaseTags.Parameters> {

    interface Parameters : ValueSourceParameters {
        val repository: DirectoryProperty
    }

    override fun obtain(): String? {
        val process = try {
            ProcessBuilder("git", "for-each-ref", "--format=%(refname:short) %(objectname)", "refs/tags/v*")
                .directory(parameters.repository.get().asFile)
                .redirectError(ProcessBuilder.Redirect.DISCARD)
                .start()
        } catch (ex: IOException) {
            return null
        }
        val output = process.inputStream.use { it.readBytes() }.toString(Charsets.UTF_8)
        return if (process.waitFor() == 0) output else null
    }
}
