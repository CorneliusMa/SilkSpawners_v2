package de.corneliusmay.silkspawners.plugin.config;

import de.corneliusmay.silkspawners.plugin.SilkSpawners;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PluginConfig {

    private final FileConfiguration config;

    public PluginConfig() {
        this.config = SilkSpawners.getInstance().getConfig();
        init();
    }

    private void init() {
        config.addDefault("messages.prefix", "$8[$bSilkSpawners$8]");
        config.addDefault("messages.locale", "en");
        config.addDefault("spawner.destroyable", true);
        config.addDefault("spawner.item.prefix", "$e");
        config.addDefault("spawner.item.prefix-old", "");
        config.addDefault("spawner.item.lore", new String[0]);
        config.addDefault("spawner.explosion.normal", 0);
        config.addDefault("spawner.explosion.silktouch", 0);
        config.addDefault("spawner.message.denyDestroy", true);
        config.addDefault("spawner.message.denyPlace", true);
        config.addDefault("spawner.message.denyChange", true);
        config.addDefault("update.check.enabled", true);
        config.addDefault("update.check.interval", 24);

        config.options().copyDefaults(true);
        SilkSpawners.getInstance().saveConfig();
        SilkSpawners.getInstance().reloadConfig();
    }

    public String getPrefix() {
        return config.getString("messages.prefix").replace("$", "§");
    }

    public String getSpawnerPrefix() {
        String prefix = config.getString("spawner.item.prefix").replace("$", "§");
        if(prefix.equals("")) prefix = "§f";
        return prefix;
    }

    public String getSpawnerPrefixOld() {
        return config.getString("spawner.item.prefix-old").replace("$", "§");
    }

    public List<String> getSpawnerLore() {
        if(config.get("spawner.item.lore") != null) return ((ArrayList<String>) config.get("spawner.item.lore")).stream().map(s -> s.replace("$", "§")).toList();
        return new ArrayList<>();
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

    public boolean sendSpawnerDestroyMessage() {
        return config.getBoolean("spawner.message.denyDestroy");
    }

    public boolean sendSpawnerPlaceMessage() {
        return config.getBoolean("spawner.message.denyPlace");
    }

    public boolean sendSpawnerChangeMessage() {
        return config.getBoolean("spawner.message.denyChange");
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
