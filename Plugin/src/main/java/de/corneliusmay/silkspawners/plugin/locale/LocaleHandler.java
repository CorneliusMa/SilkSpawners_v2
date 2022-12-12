package de.corneliusmay.silkspawners.plugin.locale;

import de.corneliusmay.silkspawners.plugin.SilkSpawners;
import de.corneliusmay.silkspawners.plugin.config.handler.ConfigValue;
import de.corneliusmay.silkspawners.plugin.config.PluginConfig;
import lombok.Getter;
import org.bukkit.entity.EntityType;

import java.io.File;
import java.io.IOException;
import java.net.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.MessageFormat;
import java.util.*;

public class LocaleHandler {

    private static final String DEFAULT_MESSAGE = "§cNo value found for key {0} using locale {1}.§7\n Use §l§n/silkspawners locale update confirm§7 to update the locale files.\n §eWarning!§7 Updating the locale files will overwrite all changes§7.";

    private final SilkSpawners plugin;

    private final File localePath;

    private final Locale locale;

    private Map<String, String> bukkitSpawnerNames;

    @Getter
    private ResourceBundle resourceBundle;

    public LocaleHandler(SilkSpawners plugin, Locale locale) {
        this.plugin = plugin;
        this.locale = locale;
        this.localePath = new File(plugin.getDataFolder() + "/locale");

        try {
            copyDefaultLocales(false);
            loadLocale();
        } catch(MissingResourceException | URISyntaxException | IOException ex) {
            plugin.getLog().error("Error loading locale file", ex);
            plugin.getLog().warn("Disabling plugin due to missing locale file");
            plugin.getLog().info("Available locales: " + getAvailableLocales());
            plugin.getPluginLoader().disablePlugin(plugin);
        }
    }

    public void copyDefaultLocales(boolean overwrite) throws URISyntaxException, IOException {
        Path target = Paths.get(plugin.getDataFolder() + "/locale");
        URI resource = getClass().getResource("").toURI();
        FileSystem fileSystem = FileSystems.newFileSystem(resource, Collections.<String, String>emptyMap());
        final Path jarPath = fileSystem.getPath("/locales");

        Files.walkFileTree(jarPath, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                Files.createDirectories(target.resolve(jarPath.relativize(dir).toString()));
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Path targetFile = target.resolve(jarPath.relativize(file).toString());
                if(overwrite) Files.copy(file, targetFile, StandardCopyOption.REPLACE_EXISTING);
                else if(Files.notExists(targetFile)) Files.copy(file, targetFile);
                return FileVisitResult.CONTINUE;
            }
        });
        fileSystem.close();
    }

    private Map<String, String> initSpawnerNames() {
        Map<String, String> spawnerNames = new HashMap<>();

        for(String key : resourceBundle.keySet()) {
            if(!key.startsWith("ENTITY_")) continue;
            String[] names =resourceBundle.getString(key).split(",");
            for(String name : names) {
                spawnerNames.put(name, key.replace("ENTITY_", ""));
            }
        }

        if(spawnerNames.size() == 0) {
            String entities = Arrays.toString(Arrays.stream(EntityType.values()).filter(EntityType::isSpawnable).map(EntityType::getName).filter(Objects::nonNull).toArray()).replace("[", "").replace("]", "").replace(" ", "");
            StringBuilder entitiesLocale = new StringBuilder("\n");
            for(String entity : entities.split(",")) {
                entitiesLocale.append("ENTITY_").append(entity.toUpperCase()).append(" = ").append(entity.substring(0, 1).toUpperCase()).append(entity.substring(1)).append("\n");
            }
            plugin.getLog().error("§cIt seems like all entity names are missing in your locale file. \n§7Please run §l§n/silkspawners locale update§7 to update the files or add the following Part: \n" + entitiesLocale);
        }

        return spawnerNames;
    }

    public void loadLocale() throws MalformedURLException {
        URL[] urls = {localePath.toURI().toURL()};
        ClassLoader loader = new URLClassLoader(urls);
        this.resourceBundle = ResourceBundle.getBundle("messages", locale, loader);
        this.bukkitSpawnerNames = initSpawnerNames();
    }

    public String getAvailableLocales() {
        File localesDir = new File(plugin.getDataFolder() + "/locale");
        return Arrays.stream(localesDir.listFiles()).sorted().map((f) -> f.getName().replace("messages_", "").replace(".properties", "")).toList().toString().replace("[", "").replace("]", "");
    }

    public String getMessageClean(String key, Object... args) throws MissingResourceException {
        return MessageFormat.format(resourceBundle.getString(key).replace("$", "§"), args);
    }

    public String getMessage(String key, Object... args) {
        try {
            return getPrefix() + "§f " + getMessageClean(key, args);
        } catch (MissingResourceException ex) {
            return getPrefix() + "§f " +  MessageFormat.format(DEFAULT_MESSAGE, key, locale.toString());
        }
    }

    public String getSpawnerEntityName(String key) {
        try {
            return getMessageClean(key).split(",")[0];
        } catch (MissingResourceException ex) {
            if(!bukkitSpawnerNames.containsKey(key)) return "§cMissing Key: " + key;
            return bukkitSpawnerNames.get(key);
        }
    }

    public static String getPrefix() {
        return new ConfigValue<String>(PluginConfig.MESSAGE_PREFIX).get();
    }
}
