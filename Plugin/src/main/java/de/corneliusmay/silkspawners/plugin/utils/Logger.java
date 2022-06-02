package de.corneliusmay.silkspawners.plugin.utils;

import org.bukkit.Bukkit;

import java.util.Arrays;

public class Logger {

    private final String prefix;

    public Logger(String prefix) {
        this.prefix = prefix;
    }

    public void info(String msg) {
        Bukkit.getConsoleSender().sendMessage(prefix + " §8[§2INFO§8]§7: " + msg);
    }

    public void warn(String msg) {
        Bukkit.getConsoleSender().sendMessage(prefix + " §8[§eWARN§8]§7: " + msg);

    }

    public void error(String msg) {
        Bukkit.getConsoleSender().sendMessage(prefix + " §8[§cERROR§8]§7: " + msg);

    }

    public void error(String msg, Throwable ex) {
        error(msg + ": §c" + ex.getMessage() + "\n§7" + Arrays.toString(ex.getStackTrace()));
    }
}
