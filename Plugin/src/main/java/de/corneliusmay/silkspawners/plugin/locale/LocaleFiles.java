package de.corneliusmay.silkspawners.plugin.locale;

import de.corneliusmay.silkspawners.plugin.utils.Logger;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.ProviderNotFoundException;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.bukkit.plugin.Plugin;

@RequiredArgsConstructor
class LocaleFiles {

    private static final String SIGNATURES = "/locale-signatures/signatures.properties";

    private final Plugin plugin;

    File path() {
        return new File(plugin.getDataFolder() + "/locale");
    }

    static String localeCode(String fileName) {
        return fileName.replace("messages_", "").replace(".properties", "");
    }

    static boolean isLocale(String fileName) {
        return fileName.startsWith("messages_") && fileName.endsWith(".properties");
    }

    synchronized void copy(boolean overwrite) throws URISyntaxException, IOException {
        Path target = path().toPath();
        Map<String, Map<String, Set<String>>> signatures = LocaleMerger.signatures(signatureLines());
        URI resource = getClass().getResource("/locales").toURI();
        List<String> failed = new ArrayList<>();
        if ("file".equalsIgnoreCase(resource.getScheme())) {
            Path jarPath = Path.of(resource);
            walkAndSync(jarPath, jarPath, target, signatures, overwrite, failed);
        } else {
            FileSystem fileSystem = null;
            boolean created = false;
            try {
                try {
                    fileSystem = FileSystems.getFileSystem(resource);
                } catch (FileSystemNotFoundException ex) {
                    fileSystem = FileSystems.newFileSystem(resource, Collections.emptyMap());
                    created = true;
                } catch (ProviderNotFoundException ex) {
                    fileSystem = FileSystems.newFileSystem(resource, Collections.emptyMap());
                    created = true;
                }
                Path jarPath = fileSystem.getPath("/locales");
                walkAndSync(jarPath, jarPath, target, signatures, overwrite, failed);
            } finally {
                if (created && fileSystem != null) {
                    fileSystem.close();
                }
            }
        }
        if (!failed.isEmpty() && overwrite) throw new IOException("Could not restore " + String.join(", ", failed));
    }

    private void walkAndSync(
            Path jarPath,
            Path startPath,
            Path target,
            Map<String, Map<String, Set<String>>> signatures,
            boolean overwrite,
            List<String> failed)
            throws IOException {
        Files.walkFileTree(startPath, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                Files.createDirectories(
                        target.resolve(jarPath.relativize(dir).toString()));
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                String relative = jarPath.relativize(file).toString();
                try {
                    List<String> bundled = read(file);
                    if (isEmpty(bundled)) return FileVisitResult.CONTINUE;
                    Map<String, Set<String>> locale = signatures.getOrDefault(localeCode(relative), Map.of());
                    sync(bundled, target.resolve(relative), locale, overwrite);
                } catch (IOException | IllegalArgumentException ex) {
                    Logger.warn("Could not " + (overwrite ? "restore " : "update ") + relative + ": "
                            + ex.getMessage());
                    failed.add(relative);
                }
                return FileVisitResult.CONTINUE;
            }
        });
    }

    private void sync(List<String> bundled, Path current, Map<String, Set<String>> signatures, boolean overwrite)
            throws IOException {
        if (overwrite || Files.notExists(current)) {
            write(current, bundled);
            return;
        }

        List<String> currentLines = read(current);
        LocaleMerger.Result result = new LocaleMerger(signatures).merge(bundled, currentLines);

        if (result.lines().equals(currentLines)) return;
        replace(current, result.lines());
        Logger.info("Updated " + current.getFileName() + ": " + result.added() + " message(s) added, "
                + result.updated() + " updated, " + result.kept() + " kept as customized");
    }

    private boolean isEmpty(List<String> lines) {
        return lines.stream().noneMatch(line -> LocaleMerger.key(line) != null);
    }

    private List<String> signatureLines() throws IOException {
        try (InputStream in = getClass().getResourceAsStream(SIGNATURES)) {
            if (in == null) {
                Logger.warn("Locale signatures are missing from the plugin jar, "
                        + "reworded messages will be kept as customizations");
                return List.of();
            }
            return decode(in.readAllBytes()).lines().toList();
        }
    }

    private List<String> read(Path file) throws IOException {
        return LocaleMerger.lines(decode(Files.readAllBytes(file)));
    }

    private String decode(byte[] bytes) {
        try {
            return StandardCharsets.UTF_8
                    .newDecoder()
                    .decode(ByteBuffer.wrap(bytes))
                    .toString();
        } catch (CharacterCodingException ex) {
            return new String(bytes, StandardCharsets.ISO_8859_1);
        }
    }

    private void write(Path file, List<String> lines) throws IOException {
        if (matches(file, lines)) return;
        replace(file, lines);
    }

    private void replace(Path file, List<String> lines) throws IOException {
        Path temporary = file.resolveSibling(file.getFileName() + ".tmp");
        try {
            Files.write(temporary, lines, StandardCharsets.UTF_8);
            move(temporary, file);
        } finally {
            Files.deleteIfExists(temporary);
        }
    }

    private boolean matches(Path file, List<String> lines) {
        try {
            return Files.exists(file) && read(file).equals(lines);
        } catch (IOException | IllegalArgumentException ex) {
            return false;
        }
    }

    private void move(Path temporary, Path file) throws IOException {
        inherit(temporary, file);
        try {
            Files.move(temporary, file, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException ex) {
            Files.move(temporary, file, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private void inherit(Path temporary, Path file) throws IOException {
        if (Files.notExists(file)) return;
        if (!file.getFileSystem().supportedFileAttributeViews().contains("posix")) return;
        Files.setPosixFilePermissions(temporary, Files.getPosixFilePermissions(file));
    }
}
