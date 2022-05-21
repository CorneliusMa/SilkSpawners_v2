package de.corneliusmay.silkspawners.plugin;

import de.corneliusmay.silkspawners.api.NMS;
import de.corneliusmay.silkspawners.plugin.commands.SilkSpawnersCommandHandler;
import de.corneliusmay.silkspawners.plugin.commands.executors.*;
import de.corneliusmay.silkspawners.plugin.config.PluginConfig;
import de.corneliusmay.silkspawners.plugin.listeners.BlockBreakListener;
import de.corneliusmay.silkspawners.plugin.listeners.BlockPlaceListener;
import de.corneliusmay.silkspawners.plugin.listeners.PlayerInteractListener;
import de.corneliusmay.silkspawners.plugin.listeners.SpawnerBreakListener;
import de.corneliusmay.silkspawners.plugin.locale.LocaleHandler;
import de.corneliusmay.silkspawners.plugin.utils.Logger;
import de.corneliusmay.silkspawners.plugin.version.VersionChecker;
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
    private PluginConfig pluginConfig;

    @Getter
    private NMS nmsHandler;

    @Getter
    private LocaleHandler locale;

    @Getter
    private VersionChecker versionChecker;
    @Getter
    private SilkSpawnersCommandHandler commandHandler;

    @Override
    public void onEnable() {
        instance = this;

        this.pluginConfig = new PluginConfig();

        this.log = new Logger();
        log.info("Starting SilkSpawners v" + VersionChecker.getInstalledVersion());

        log.info("Loading Cross-Version support");
        VersionHandler versionHandler = new VersionHandler();
        if(!versionHandler.load()) return;
        nmsHandler = versionHandler.getNmsHandler();

        log.info("Loading locale file");
        locale = new LocaleHandler(pluginConfig.getLocale());
        if(locale.getResourceBundle() == null) return;

        versionChecker = new VersionChecker();
        if(pluginConfig.checkForUpdates()) startUpdateChecker();
        else log.warn("Update checking is disabled");

        log.info("Starting metrics service. You can disable the collection of anonymous usage data by editing the config file under /plugins/bStats/");
        new Metrics(this, 15215);

        log.info("Registering listeners");
        registerListeners();

        log.info("Registering commands");
        registerCommands();

        log.info("Started SilkSpawners v" + VersionChecker.getInstalledVersion());
    }

    private void startUpdateChecker() {
        log.info("Starting update checker");
        versionChecker.start(pluginConfig.getUpdateCheckInterval());
    }

    private void registerListeners() {
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new BlockBreakListener(), this);
        pm.registerEvents(new BlockPlaceListener(), this);
        pm.registerEvents(new SpawnerBreakListener(), this);
        pm.registerEvents(new PlayerInteractListener(), this);
    }

    private void registerCommands() {
        commandHandler = new SilkSpawnersCommandHandler();
        commandHandler.registerCommand(new HelpCommand());
        commandHandler.registerCommand(new GiveCommand());
        commandHandler.registerCommand(new PermissionsCommand());
        commandHandler.registerCommand(new ExplosionCommand());
        commandHandler.registerCommand(new VersionCommand());
        commandHandler.registerCommand(new LocaleCommand());

        getCommand("silkspawners").setExecutor(commandHandler);
        getCommand("silkspawners").setTabCompleter(commandHandler.getTabCompleter());
    }

    @Override
    public void onDisable() {
        if(versionChecker == null) return;
        log.info("Stopping version checker");
        versionChecker.stop();
    }
}
