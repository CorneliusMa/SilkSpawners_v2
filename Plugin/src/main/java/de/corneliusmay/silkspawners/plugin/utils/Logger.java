package de.corneliusmay.silkspawners.plugin.utils;

import de.corneliusmay.silkspawners.plugin.config.PluginConfig;
import java.util.Arrays;
import org.bukkit.Bukkit;
import org.weftkit.wiring.Requires;
import org.weftkit.wiring.Singleton;
import org.weftkit.wiring.Wired;

@Wired
@Singleton
@Requires(PluginConfig.class)
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

    public void warn(String msg, Throwable ex) {
        warn(describe(msg, ex));
    }

    public void error(String msg) {
        Bukkit.getConsoleSender().sendMessage(getPrefix() + " §8[§cERROR§8]§7: " + msg);
    }

    public void error(String msg, Throwable ex) {
        error(describe(msg, ex));
    }

    private String describe(String msg, Throwable ex) {
        return msg + ": §c" + ex.getMessage() + "\n§7" + Arrays.toString(ex.getStackTrace());
    }
}
