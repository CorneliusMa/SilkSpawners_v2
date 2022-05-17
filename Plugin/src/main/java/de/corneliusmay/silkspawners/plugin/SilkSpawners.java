package de.corneliusmay.silkspawners.plugin;

import de.corneliusmay.silkspawners.api.NMS;
import de.corneliusmay.silkspawners.plugin.commands.SpawnerCommand;
import de.corneliusmay.silkspawners.plugin.config.PluginConfig;
import de.corneliusmay.silkspawners.plugin.listeners.BlockBreakListener;
import de.corneliusmay.silkspawners.plugin.listeners.BlockPlaceListener;
import de.corneliusmay.silkspawners.plugin.listeners.SpawnerBreakListener;
import de.corneliusmay.silkspawners.plugin.utils.Logger;
import de.corneliusmay.silkspawners.plugin.version.VersionHandler;
import lombok.Getter;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class SilkSpawners extends JavaPlugin {
    @Getter
    private static SilkSpawners instance;

    @Getter
    private Logger log;

    @Getter
    PluginConfig pluginConfig;

    @Getter
    private NMS nmsHandler;

    @Override
    public void onEnable() {
        instance = this;

        this.pluginConfig = new PluginConfig();

        this.log = new Logger();
        log.info("Starting SilkSpawners v" + getDescription().getVersion());

        log.info("Loading Cross-Version support");
        VersionHandler versionHandler = new VersionHandler();
        if(!versionHandler.load()) return;
        nmsHandler = versionHandler.getNmsHandler();
        log.info("Loaded Cross-Version support");

        log.info("Starting metrics service. You can disable the collection of anonymous usage data by editing the config file under /plugins/bStats/");
        new Metrics(this, 15215);

        log.info("Registering listeners");
        registerListeners();
        log.info("Registered listeners");

        log.info("Registering commands");
        registerCommands();
        log.info("Registered commands");

        log.info("Started SilkSpawners v" + getDescription().getVersion());
    }

    private void registerListeners() {
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new BlockBreakListener(), this);
        pm.registerEvents(new BlockPlaceListener(), this);
        pm.registerEvents(new SpawnerBreakListener(), this);
    }

    private void registerCommands() {
        getCommand("spawner").setExecutor(new SpawnerCommand());
        getCommand("spawner").setTabCompleter(new SpawnerCommand());
    }

    @Override
    public void onDisable() {

    }
}
