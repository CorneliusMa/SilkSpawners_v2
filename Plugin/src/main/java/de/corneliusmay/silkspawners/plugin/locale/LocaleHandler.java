package de.corneliusmay.silkspawners.plugin.locale;

import de.corneliusmay.silkspawners.plugin.config.PluginConfig;
import de.corneliusmay.silkspawners.plugin.utils.Logger;
import de.corneliusmay.silkspawners.plugin.utils.MessageRenderer;
import de.corneliusmay.silkspawners.plugin.utils.MixedFormattingException;
import de.corneliusmay.silkspawners.wiring.Loader;
import de.corneliusmay.silkspawners.wiring.Requires;
import de.corneliusmay.silkspawners.wiring.Wired;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.plugin.Plugin;

@Wired
@Requires(PluginConfig.class)
@RequiredArgsConstructor
public class LocaleHandler implements Loader {

    private static final String DEFAULT_MESSAGE =
            "§cNo value found for key {0} using locale {1}.§7\n Use §l§n/silkspawners locale update confirm§7 to update the locale files.\n §eWarning!§7 Updating the locale files will overwrite all changes§7.";

    private static final String MIXED_MESSAGE =
            "§cThe message for key {0} mixes legacy formatting codes with MiniMessage tags, which is not supported.§7\n Use §l§neither legacy codes or MiniMessage tags§7 for a message, not both.";

    private static final String INCOMPLETE_WARNING =
            "The locale {0} is incomplete ({1}% translated). Untranslated messages will be shown in English. You can help completing the translation at {2}";

    public static final String CROWDIN_URL = "https://crowdin.com/project/silkspawners";

    private final Plugin plugin;

    @Getter
    private volatile ResourceBundle resourceBundle;

    private volatile ResourceBundle fallbackBundle;

    private volatile ResourceBundle bundledFallback;

    @Getter
    private volatile int completionPercent;

    private volatile Locale loadedLocale;

    private File localePath() {
        return new File(plugin.getDataFolder() + "/locale");
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
        Path target = localePath().toPath();
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
                    if (isEmptyBundle(file)) return FileVisitResult.CONTINUE;
                    Path targetFile = target.resolve(jarPath.relativize(file).toString());
                    if (overwrite) Files.copy(file, targetFile, StandardCopyOption.REPLACE_EXISTING);
                    else if (Files.notExists(targetFile)) Files.copy(file, targetFile);
                    return FileVisitResult.CONTINUE;
                }
            });
        }
    }

    public synchronized void loadLocale() throws IOException, MissingResourceException {
        URL[] urls = {localePath().toURI().toURL()};
        ClassLoader loader = new URLClassLoader(urls);
        try (InputStream in = getClass().getResourceAsStream("/locales/messages_en.properties")) {
            this.bundledFallback = new PropertyResourceBundle(in);
        }
        Locale locale = getLocale();
        this.fallbackBundle = ResourceBundle.getBundle("messages", Locale.ENGLISH, loader);
        this.resourceBundle = ResourceBundle.getBundle("messages", locale, loader);
        this.loadedLocale = locale;
        this.completionPercent = computeCompletionPercent();
        if (isIncomplete())
            Logger.warn(MessageFormat.format(INCOMPLETE_WARNING, locale, completionPercent, CROWDIN_URL));
    }

    public boolean isSelectedLocaleLoaded() {
        return getLocale().equals(loadedLocale);
    }

    public boolean isIncomplete() {
        return completionPercent < 100;
    }

    private int computeCompletionPercent() {
        Set<String> reference = referenceKeys();
        if (reference.isEmpty()) return 100;
        long translated = reference.stream().filter(resourceBundle::containsKey).count();
        return (int) (100 * translated / reference.size());
    }

    private Locale getLocale() {
        return PluginConfig.MESSAGE_LOCALE.get();
    }

    public String getLocaleDisplayName() {
        Locale locale = getLocale();
        String displayName = locale.getDisplayName(Locale.ENGLISH);
        // Unrecognized codes (eg. a self-made "test" locale) echo back as-is instead of resolving to a language name
        return displayName.equalsIgnoreCase(locale.getLanguage()) ? "Custom" : displayName;
    }

    public String getAvailableLocales() {
        File[] files = localePath().listFiles();
        if (files == null) return "";
        Set<String> reference = referenceKeys();
        return Arrays.stream(files)
                .sorted()
                .map((f) -> describeLocale(f, reference))
                .filter(Objects::nonNull)
                .collect(Collectors.joining(", "));
    }

    private String describeLocale(File file, Set<String> reference) {
        Set<String> keys = loadProperties(file.toPath()).stringPropertyNames();
        if (keys.isEmpty()) return null;
        String name = file.getName().replace("messages_", "").replace(".properties", "");
        if (reference.isEmpty()) return name;
        long translated = keys.stream().filter(reference::contains).count();
        return name + " (" + (100 * translated / reference.size()) + "%)";
    }

    public String getMessageClean(String key, Object... args) {
        return MessageRenderer.render(getRawMessage(key).replace("$", "§"), args);
    }

    private String getRawMessage(String key) {
        try {
            return resourceBundle.getString(key);
        } catch (MissingResourceException ex) {
            try {
                return fallbackBundle.getString(key);
            } catch (MissingResourceException fallbackEx) {
                return bundledFallback.getString(key);
            }
        }
    }

    private boolean isEmptyBundle(Path file) {
        return loadProperties(file).isEmpty();
    }

    private Set<String> referenceKeys() {
        Properties properties = new Properties();
        try (InputStream in = getClass().getResourceAsStream("/locales/messages_en.properties")) {
            if (in != null) properties.load(in);
        } catch (IOException ignored) {
        }
        return properties.stringPropertyNames();
    }

    private Properties loadProperties(Path file) {
        Properties properties = new Properties();
        try (InputStream in = Files.newInputStream(file)) {
            properties.load(in);
        } catch (IOException ex) {
            // Unreadable files are treated as empty and thus not offered as locales
        }
        return properties;
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
