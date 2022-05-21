package de.corneliusmay.silkspawners.plugin.config;

import de.corneliusmay.silkspawners.plugin.SilkSpawners;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Locale;

public class PluginConfig {

    private final FileConfiguration config;

    public PluginConfig() {
        this.config = SilkSpawners.getInstance().getConfig();
        init();
    }

    private void init() {
        config.addDefault("messages.prefix", "§b[SilkSpawners]");
        config.addDefault("messages.locale", "en");
        config.addDefault("spawner.explosion.normal", 0);
        config.addDefault("spawner.explosion.silktouch", 0);
        config.addDefault("spawner.destroyable", true);
        config.addDefault("update.check.enabled", true);
        config.addDefault("update.check.interval", 24);

        config.options().copyDefaults(true);
        SilkSpawners.getInstance().saveConfig();
        SilkSpawners.getInstance().reloadConfig();
    }

    public String getPrefix() {
        return config.getString("messages.prefix");
    }

    public Locale getLocale() {
        return Locale.forLanguageTag(config.getString("messages.locale"));
    }

    public int getSpawnerExplosion() {
        return config.getInt("spawner.explosion.normal");
    }

    public int getSpawnerExplosionSilktouch() {
        return config.getInt("spawner.explosion.silktouch");
    }

    public boolean isSpawnerDestroyable() {
        return config.getBoolean("spawner.destroyable");
    }

    public boolean checkForUpdates() {
        return config.getBoolean("update.check.enabled");
    }

    public int getUpdateCheckInterval() {
        return config.getInt("update.check.interval");
    }
}
