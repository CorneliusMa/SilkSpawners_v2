package de.corneliusmay.silkspawners.plugin.config;

import de.corneliusmay.silkspawners.plugin.SilkSpawners;
import org.bukkit.configuration.file.FileConfiguration;

public class PluginConfig {

    private final FileConfiguration config;

    public PluginConfig() {
        this.config = SilkSpawners.getInstance().getConfig();
        init();
    }

    private void init() {
        config.addDefault("messages.prefix", "§b[SilkSpawners]");
        config.addDefault("messages.insufficient_permission", "§cYou dont have the Permission to execute this command.");
        config.addDefault("spawner.explosion.normal", 0);
        config.addDefault("spawner.explosion.silktouch", 0);

        config.options().copyDefaults(true);
        SilkSpawners.getInstance().saveConfig();
        SilkSpawners.getInstance().reloadConfig();
    }

    public String getPrefix() {
        return config.getString("messages.prefix");
    }

    public String getInsufficientPermissionMessage() {
        return config.getString("messages.insufficient_permission");
    }

    public int getSpawnerExplosion() {
        return config.getInt("spawner.explosion.normal");
    }

    public int getSpawnerExplosionSilktouch() {
        return config.getInt("spawner.explosion.silktouch");
    }
}
