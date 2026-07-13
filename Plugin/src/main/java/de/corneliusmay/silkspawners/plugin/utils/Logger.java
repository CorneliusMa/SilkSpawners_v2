package de.corneliusmay.silkspawners.plugin.utils;

import de.corneliusmay.silkspawners.plugin.config.PluginConfig;
import java.util.Arrays;
import org.bukkit.Bukkit;

public class Logger {

    private String getPrefix() {
        return PluginConfig.MESSAGE_PREFIX.get();
    }

    public void info(String msg) {
        Bukkit.getConsoleSender().sendMessage(getPrefix() + " §8[§2INFO§8]§7: " + msg);
    }

    public void warn(String msg) {
        Bukkit.getConsoleSender().sendMessage(getPrefix() + " §8[§eWARN§8]§7: " + msg);
    }

    public void error(String msg) {
        Bukkit.getConsoleSender().sendMessage(getPrefix() + " §8[§cERROR§8]§7: " + msg);
    }

    public void error(String msg, Throwable ex) {
        error(msg + ": §c" + ex.getMessage() + "\n§7" + Arrays.toString(ex.getStackTrace()));
    }
}
