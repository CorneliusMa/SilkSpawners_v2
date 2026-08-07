package de.corneliusmay.silkspawners.plugin.config;

import de.corneliusmay.silkspawners.plugin.locale.LocaleHandler;
import de.corneliusmay.silkspawners.plugin.utils.MixedFormattingException;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.MissingResourceException;
import lombok.RequiredArgsConstructor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.weftkit.wiring.Requires;
import org.weftkit.wiring.Singleton;
import org.weftkit.wiring.Wired;

@Wired
@Singleton
@Requires(PluginConfig.class)
@RequiredArgsConstructor
public class ConfigEditor {

    private final Plugin plugin;

    private final LocaleHandler locale;

    public List<String> settablePaths() {
        return PluginConfig.values().stream()
                .filter(ConfigKey::isSettable)
                .map(ConfigKey::getPath)
                .toList();
    }

    public ConfigKey<?> find(String path) {
        ConfigKey<?> key = PluginConfig.byPath(path);
        return key == null || key.isInternal() ? null : key;
    }

    public List<String> allowedValues(ConfigKey<?> key) {
        if (key == PluginConfig.MESSAGE_LOCALE) return locale.getLocaleCodes();
        return key.getSuggestions();
    }

    public String allowedValue(ConfigKey<?> key, String input) {
        List<String> allowed = allowedValues(key);
        if (allowed.isEmpty()) return input;
        return allowed.stream().filter(input::equalsIgnoreCase).findFirst().orElse(null);
    }

    public String currentValue(ConfigKey<?> key) {
        Object value = plugin.getConfig().get(key.getPath());
        return value == null ? "" : String.valueOf(value);
    }

    public String description(ConfigKey<?> key) {
        try {
            return locale.getMessageClean(key.getDescriptionKey());
        } catch (MissingResourceException | MixedFormattingException ex) {
            return "";
        }
    }

    public void set(ConfigKey<?> key, String value) throws IOException {
        set(key, key.parse(value));
    }

    public void set(ConfigKey<?> key, Object value) throws IOException {
        write(key, value);
        if (key.getApply() == ConfigApply.IMMEDIATELY) key.publish(value);
    }

    private void write(ConfigKey<?> key, Object value) throws IOException {
        FileConfiguration config = plugin.getConfig();
        Object previous = config.get(key.getPath());
        config.set(key.getPath(), value);
        try {
            config.save(new File(plugin.getDataFolder(), "config.yml"));
        } catch (IOException ex) {
            config.set(key.getPath(), previous);
            throw ex;
        }
    }
}
