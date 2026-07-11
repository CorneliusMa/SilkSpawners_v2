package de.corneliusmay.silkspawners.plugin;

import de.corneliusmay.silkspawners.api.Bukkit;
import de.corneliusmay.silkspawners.api.ServerPlatform;
import de.corneliusmay.silkspawners.plugin.commands.*;
import de.corneliusmay.silkspawners.plugin.commands.handler.SilkSpawnersCommandHandler;
import de.corneliusmay.silkspawners.plugin.config.handler.ConfigLoader;
import de.corneliusmay.silkspawners.plugin.listeners.BlockBreakListener;
import de.corneliusmay.silkspawners.plugin.listeners.BlockPlaceListener;
import de.corneliusmay.silkspawners.plugin.listeners.PlayerInteractListener;
import de.corneliusmay.silkspawners.plugin.listeners.SpawnerBreakListener;
import de.corneliusmay.silkspawners.plugin.listeners.handler.SilkSpawnersEventHandler;
import de.corneliusmay.silkspawners.plugin.locale.LocaleHandler;
import de.corneliusmay.silkspawners.plugin.platform.PlatformLoader;
import de.corneliusmay.silkspawners.plugin.utils.Logger;
import de.corneliusmay.silkspawners.plugin.version.VersionChecker;
import de.corneliusmay.silkspawners.plugin.version.CrossVersionHandler;
import lombok.Getter;
import org.bstats.bukkit.Metrics;
import org.bukkit.block.Block;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public class SilkSpawners extends JavaPlugin {

    private Logger log;

    private ServerPlatform platform;

    private Bukkit bukkitHandler;

    private LocaleHandler locale;

    private VersionChecker versionChecker;

    private ConfigLoader configLoader;

    @Override
    public void onEnable() {
        configLoader = new ConfigLoader(this);
        if(!configLoader.isLoaded()) return;

        log = new Logger();

        versionChecker = new VersionChecker(this);
        versionChecker.start();

        log.info("Starting SilkSpawners v" + versionChecker.getInstalledVersion());

        log.info("Loading server platform");
        PlatformLoader platformLoader = new PlatformLoader(this);
        platformLoader.load();
        platform = platformLoader.getServerPlatform();

        log.info("Loading Cross-Version support");
        CrossVersionHandler versionHandler = new CrossVersionHandler(this);
        if(!versionHandler.load()) return;
        bukkitHandler = versionHandler.getBukkitHandler();

        log.info("Loading locale file");
        locale = new LocaleHandler(this);
        if(locale.getResourceBundle() == null) return;

        log.info("Starting bStats integration");
        new Metrics(this, 15215);

        log.info("Registering listeners");
        registerListeners();

        log.info("Registering commands");
        registerCommands();

        log.info("Started SilkSpawners v" + versionChecker.getInstalledVersion());
    }

    private void registerListeners() {
        List<Block> editedSpawners = Collections.synchronizedList(new ArrayList<>());
        SilkSpawnersEventHandler eventHandler = new SilkSpawnersEventHandler(this);
        eventHandler.registerListener(new PlayerInteractListener(editedSpawners));
        eventHandler.registerListener(new BlockPlaceListener(editedSpawners));
        eventHandler.registerListener(new BlockBreakListener());
        eventHandler.registerListener(new SpawnerBreakListener());
    }

    private void registerCommands() {
        SilkSpawnersCommandHandler commandHandler = new SilkSpawnersCommandHandler(this, "silkspawners");
        commandHandler.addCommand(new GiveCommand());
        commandHandler.addCommand(new SetCommand());
        commandHandler.addCommand(new ExplosionCommand());
        commandHandler.addCommand(new VersionCommand());
        commandHandler.addCommand(new LocaleCommand());
        commandHandler.addCommand(new ConfigCommand());
        commandHandler.addCommand(new EntitiesCommand());
        commandHandler.register();
    }

    public boolean reloadConfiguration() {
        if(!configLoader.reload()) return false;
        versionChecker.restart();
        return true;
    }

    @Override
    public void onDisable() {
        if(versionChecker == null) return;
        log.info("Stopping version checker");
        versionChecker.stop();
    }
}
