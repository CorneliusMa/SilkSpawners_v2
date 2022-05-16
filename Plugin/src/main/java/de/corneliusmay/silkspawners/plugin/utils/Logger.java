package de.corneliusmay.silkspawners.plugin.utils;

import org.bukkit.Bukkit;

import java.util.Arrays;

public class Logger {

    private final String PREFIX;

    public Logger() {
        this.PREFIX = " §b[SilkSpawners] ";
    }

    public void info(String msg) {
        Bukkit.getConsoleSender().sendMessage(this.PREFIX + " §2[INFO] §f" + msg);
    }

    public void warn(String msg) {
        Bukkit.getConsoleSender().sendMessage(this.PREFIX + " §e[WARN] §f" + msg);

    }

    public void error(String msg) {
        Bukkit.getConsoleSender().sendMessage(this.PREFIX + " §c[ERROR] §f" + msg);

    }

    public void error(String msg, Throwable ex) {
        error(msg + ": §c" + ex.getMessage() + "\n§7" + Arrays.toString(ex.getStackTrace()));
    }
}
