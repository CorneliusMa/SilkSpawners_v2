package de.corneliusmay.silkspawners.plugin.dump;

import de.corneliusmay.silkspawners.plugin.config.ConfigLoader;
import de.corneliusmay.silkspawners.plugin.dump.sections.EnvironmentSection;
import de.corneliusmay.silkspawners.plugin.dump.sections.MetaSection;
import de.corneliusmay.silkspawners.plugin.dump.sections.PluginListSection;
import de.corneliusmay.silkspawners.plugin.dump.sections.ServerSection;
import de.corneliusmay.silkspawners.plugin.hooks.HookLoader;
import de.corneliusmay.silkspawners.plugin.locale.LocaleHandler;
import de.corneliusmay.silkspawners.plugin.message.InteractiveMessageLoader;
import de.corneliusmay.silkspawners.plugin.platform.PlatformLoader;
import de.corneliusmay.silkspawners.plugin.utils.Logger;
import de.corneliusmay.silkspawners.plugin.version.CrossVersionHandler;
import de.corneliusmay.silkspawners.plugin.version.VersionChecker;
import de.corneliusmay.silkspawners.wiring.Wired;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Consumer;
import org.bukkit.plugin.Plugin;

@Wired
public class Dump {

    private static final DateTimeFormatter FALLBACK_TIMESTAMP = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");

    private final Plugin plugin;

    private final List<Dumpable> sections;

    public Dump(
            Plugin plugin,
            VersionChecker versionChecker,
            CrossVersionHandler crossVersionHandler,
            PlatformLoader platformLoader,
            LocaleHandler localeHandler,
            InteractiveMessageLoader interactiveMessageLoader,
            ConfigLoader configLoader,
            HookLoader hookLoader) {
        this.plugin = plugin;
        this.sections = List.of(
                new MetaSection(),
                versionChecker,
                new ServerSection(),
                crossVersionHandler,
                platformLoader,
                new EnvironmentSection(),
                localeHandler,
                interactiveMessageLoader,
                hookLoader,
                configLoader,
                new PluginListSection());
    }

    public void create(Consumer<String> uploaded, Consumer<Path> saved) {
        String document = new DumpReport().collect(sections).render();
        Pastes.upload(document).whenComplete((url, ex) -> {
            if (ex == null) uploaded.accept(url);
            else {
                Logger.error("Error uploading dump", ex);
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
            Logger.error("Error saving dump", ex);
            return file;
        }
    }
}
