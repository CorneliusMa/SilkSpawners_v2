package de.corneliusmay.silkspawners.plugin.dump;

import de.corneliusmay.silkspawners.plugin.utils.Logger;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import org.bukkit.plugin.Plugin;
import org.weftkit.wiring.Wired;
import org.weftkit.wiring.runtime.WeftLoader;

@Wired
@RequiredArgsConstructor
public class Dump {

    private static final DateTimeFormatter FALLBACK_TIMESTAMP = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");

    // Sections not listed here render after the listed ones
    private static final List<String> SECTION_ORDER = List.of(
            "meta",
            "plugin",
            "server",
            "version-adapter",
            "platform",
            "environment",
            "java",
            "os",
            "locale",
            "hooks",
            "config",
            "wiring",
            "plugins");

    private final Plugin plugin;

    private final Pastes pastes;

    private final WeftLoader loader;

    private final Logger logger;

    public void create(Consumer<String> uploaded, Consumer<Path> saved) {
        String document = new DumpReport()
                .collect(loader.createAll(Dumpable.class))
                .order(SECTION_ORDER)
                .render();
        pastes.upload(document).whenComplete((url, ex) -> {
            if (ex == null) uploaded.accept(url);
            else {
                logger.error("Error uploading dump", ex);
                saved.accept(save(document));
            }
        });
    }

    private Path save(String document) {
        Path file = plugin.getDataFolder()
                .toPath()
                .resolve("dump-" + LocalDateTime.now().format(FALLBACK_TIMESTAMP) + ".json");
        try {
            return Files.writeString(file, document);
        } catch (IOException ex) {
            logger.error("Error saving dump", ex);
            return file;
        }
    }
}
