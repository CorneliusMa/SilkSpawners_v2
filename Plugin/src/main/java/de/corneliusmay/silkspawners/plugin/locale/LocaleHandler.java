package de.corneliusmay.silkspawners.plugin.locale;

import de.corneliusmay.silkspawners.plugin.SilkSpawners;
import de.corneliusmay.silkspawners.plugin.config.ConfigLoader;
import de.corneliusmay.silkspawners.plugin.config.PluginConfig;
import de.corneliusmay.silkspawners.plugin.utils.Logger;
import de.corneliusmay.silkspawners.plugin.utils.MessageRenderer;
import de.corneliusmay.silkspawners.plugin.utils.MixedFormattingException;
import de.corneliusmay.silkspawners.wiring.Loader;
import de.corneliusmay.silkspawners.wiring.Wired;
import java.io.File;
import java.io.IOException;
import java.net.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.MessageFormat;
import java.util.*;
import lombok.Getter;

@Wired
public class LocaleHandler implements Loader {

    private static final String DEFAULT_MESSAGE =
            "§cNo value found for key {0} using locale {1}.§7\n Use §l§n/silkspawners locale update confirm§7 to update the locale files.\n §eWarning!§7 Updating the locale files will overwrite all changes§7.";

    private static final String MIXED_MESSAGE =
            "§cThe message for key {0} mixes legacy formatting codes with MiniMessage tags, which is not supported.§7\n Use §l§neither legacy codes or MiniMessage tags§7 for a message, not both.";

    private final SilkSpawners plugin;

    // Only exists to make the config load before the locale
    private final ConfigLoader config;

    private final File localePath;

    @Getter
    private volatile ResourceBundle resourceBundle;

    public LocaleHandler(SilkSpawners plugin, ConfigLoader config) {
        this.plugin = plugin;
        this.config = config;
        this.localePath = new File(plugin.getDataFolder() + "/locale");
    }

    @Override
    public boolean load() {
        Logger.info("Loading locale file");
        try {
            copyDefaultLocales(false);
            loadLocale();
            return true;
        } catch (MissingResourceException | URISyntaxException | IOException ex) {
            Logger.error("Error loading locale file", ex);
            Logger.warn("Disabling plugin due to missing locale file");
            Logger.info("Available locales: " + getAvailableLocales());
            return false;
        }
    }

    public synchronized void copyDefaultLocales(boolean overwrite) throws URISyntaxException, IOException {
        Path target = Paths.get(plugin.getDataFolder() + "/locale");
        URI resource = getClass().getResource("").toURI();
        try (FileSystem fileSystem = FileSystems.newFileSystem(resource, Collections.<String, String>emptyMap())) {
            final Path jarPath = fileSystem.getPath("/locales");

            Files.walkFileTree(jarPath, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    Files.createDirectories(
                            target.resolve(jarPath.relativize(dir).toString()));
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Path targetFile = target.resolve(jarPath.relativize(file).toString());
                    if (overwrite) Files.copy(file, targetFile, StandardCopyOption.REPLACE_EXISTING);
                    else if (Files.notExists(targetFile)) Files.copy(file, targetFile);
                    return FileVisitResult.CONTINUE;
                }
            });
        }
    }

    public synchronized void loadLocale() throws MalformedURLException {
        URL[] urls = {localePath.toURI().toURL()};
        ClassLoader loader = new URLClassLoader(urls);
        this.resourceBundle = ResourceBundle.getBundle("messages", getLocale(), loader);
    }

    private Locale getLocale() {
        return PluginConfig.MESSAGE_LOCALE.get();
    }

    public String getAvailableLocales() {
        File localesDir = new File(plugin.getDataFolder() + "/locale");
        return Arrays.stream(localesDir.listFiles())
                .sorted()
                .map((f) -> f.getName().replace("messages_", "").replace(".properties", ""))
                .toList()
                .toString()
                .replace("[", "")
                .replace("]", "");
    }

    public String getMessageClean(String key, Object... args) {
        return MessageRenderer.render(resourceBundle.getString(key).replace("$", "§"), args);
    }

    public String getMessage(String key, Object... args) {
        try {
            return getPrefix() + "§f " + getMessageClean(key, args);
        } catch (MissingResourceException ex) {
            return getPrefix() + "§f "
                    + MessageFormat.format(DEFAULT_MESSAGE, key, getLocale().toString());
        } catch (MixedFormattingException ex) {
            return getPrefix() + "§f " + MessageFormat.format(MIXED_MESSAGE, key);
        }
    }

    private String getPrefix() {
        return PluginConfig.MESSAGE_PREFIX.get();
    }
}
