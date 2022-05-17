package de.corneliusmay.silkspawners.plugin.utils;

import de.corneliusmay.silkspawners.plugin.SilkSpawners;
import org.bukkit.Bukkit;

import java.util.Arrays;

public class Logger {

    public void info(String msg) {
        Bukkit.getConsoleSender().sendMessage(SilkSpawners.getInstance().getPluginConfig().getPrefix() + " §2[INFO] §f" + msg);
    }

    public void warn(String msg) {
        Bukkit.getConsoleSender().sendMessage(SilkSpawners.getInstance().getPluginConfig().getPrefix() + " §e[WARN] §f" + msg);

    }

    public void error(String msg) {
        Bukkit.getConsoleSender().sendMessage(SilkSpawners.getInstance().getPluginConfig().getPrefix() + " §c[ERROR] §f" + msg);

    }

    public void error(String msg, Throwable ex) {
        error(msg + ": §c" + ex.getMessage() + "\n§7" + Arrays.toString(ex.getStackTrace()));
    }
}
