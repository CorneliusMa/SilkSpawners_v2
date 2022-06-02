package de.corneliusmay.silkspawners.plugin;

import de.corneliusmay.silkspawners.api.NMS;
import de.corneliusmay.silkspawners.plugin.commands.SilkSpawnersCommandHandler;
import de.corneliusmay.silkspawners.plugin.commands.executors.*;
import de.corneliusmay.silkspawners.plugin.config.PluginConfig;
import de.corneliusmay.silkspawners.plugin.config.handler.ConfigLoader;
import de.corneliusmay.silkspawners.plugin.config.handler.ConfigValue;
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

import java.util.Locale;

public class SilkSpawners extends JavaPlugin {

    @Getter
    private Logger log;

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
        new ConfigLoader(this).load();

        log = new Logger(new ConfigValue<String>(PluginConfig.MESSAGE_PREFIX).get());

        versionChecker = new VersionChecker(this);
        if(new ConfigValue<Boolean>(PluginConfig.UPDATE_CHECK_ENABLED).get()) versionChecker.start(new ConfigValue<Integer>(PluginConfig.UPDATE_CHECK_INTERVAL).get());
        else log.warn("Update checking is disabled");

        log.info("Starting SilkSpawners v" + versionChecker.getInstalledVersion());

        log.info("Loading Cross-Version support");
        VersionHandler versionHandler = new VersionHandler(this);
        if(!versionHandler.load()) return;
        nmsHandler = versionHandler.getNmsHandler();

        log.info("Loading locale file");
        locale = new LocaleHandler(this, new ConfigValue<Locale>(PluginConfig.MESSAGE_LOCALE).get());
        if(locale.getResourceBundle() == null) return;

        log.info("Starting metrics service. You can disable the collection of anonymous usage data by editing the config file under /plugins/bStats/");
        new Metrics(this, 15215);

        log.info("Registering listeners");
        registerListeners();

        log.info("Registering commands");
        registerCommands();

        log.info("Started SilkSpawners v" + versionChecker.getInstalledVersion());
    }

    private void registerListeners() {
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new PlayerInteractListener(this), this);
        pm.registerEvents(new BlockPlaceListener(this), this);
        pm.registerEvents(new BlockBreakListener(this), this);
        pm.registerEvents(new SpawnerBreakListener(), this);
    }

    private void registerCommands() {
        commandHandler = new SilkSpawnersCommandHandler(this);
        commandHandler.registerCommand(new HelpCommand(commandHandler));
        commandHandler.registerCommand(new GiveCommand());
        commandHandler.registerCommand(new SetCommand());
        commandHandler.registerCommand(new ExplosionCommand());
        commandHandler.registerCommand(new VersionCommand());
        commandHandler.registerCommand(new LocaleCommand());
        commandHandler.registerCommand(new PermissionsCommand());

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
