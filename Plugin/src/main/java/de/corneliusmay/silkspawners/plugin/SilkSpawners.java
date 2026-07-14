package de.corneliusmay.silkspawners.plugin;

import de.corneliusmay.silkspawners.plugin.api.SilkSpawnersService;
import de.corneliusmay.silkspawners.plugin.commands.*;
import de.corneliusmay.silkspawners.plugin.commands.handler.SilkSpawnersCommandHandler;
import de.corneliusmay.silkspawners.plugin.config.ConfigLoader;
import de.corneliusmay.silkspawners.plugin.config.PluginConfig;
import de.corneliusmay.silkspawners.plugin.hooks.HookLoader;
import de.corneliusmay.silkspawners.plugin.listeners.BlockBreakListener;
import de.corneliusmay.silkspawners.plugin.listeners.BlockPlaceListener;
import de.corneliusmay.silkspawners.plugin.listeners.PlayerInteractListener;
import de.corneliusmay.silkspawners.plugin.listeners.SpawnerBreakListener;
import de.corneliusmay.silkspawners.plugin.listeners.handler.SilkSpawnersEventHandler;
import de.corneliusmay.silkspawners.plugin.loader.PluginLoader;
import de.corneliusmay.silkspawners.plugin.locale.LocaleHandler;
import de.corneliusmay.silkspawners.plugin.platform.PlatformLoader;
import de.corneliusmay.silkspawners.plugin.spawner.SpawnerLoader;
import de.corneliusmay.silkspawners.plugin.utils.Logger;
import de.corneliusmay.silkspawners.plugin.version.CrossVersionHandler;
import de.corneliusmay.silkspawners.plugin.version.VersionChecker;
import de.corneliusmay.silkspawners.spi.platform.ServerPlatform;
import de.corneliusmay.silkspawners.spi.version.Bukkit;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Getter;
import org.bstats.bukkit.Metrics;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

public class SilkSpawners extends JavaPlugin {

    @Getter
    private Logger log;

    @Getter
    private VersionChecker versionChecker;

    private PluginLoader loader;

    @Override
    public void onEnable() {
        log = new Logger();
        loader = new PluginLoader(this);

        ConfigLoader config = new ConfigLoader(this);
        PlatformLoader platformLoader = new PlatformLoader(this);
        CrossVersionHandler versionHandler = new CrossVersionHandler(this);
        if (!loader.load(
                config,
                platformLoader,
                versionHandler,
                new SpawnerLoader(versionHandler, platformLoader),
                new LocaleHandler(this))) return;

        registerListeners();
        registerCommands();
        registerApiService();
        registerHooks();
        startOptional(this::startVersionChecker);
        startOptional(this::startMetrics);

        log.info("Enabled SilkSpawners v" + versionChecker.getInstalledVersion());
    }

    private void startOptional(Runnable step) {
        try {
            step.run();
        } catch (RuntimeException ex) {
            log.error("An optional startup step threw and was skipped", ex);
        }
    }

    private void startVersionChecker() {
        versionChecker = new VersionChecker(this);
        versionChecker.start();
    }

    private void startMetrics() {
        log.info("Starting bStats integration");
        new Metrics(this, 15215);
    }

    private void registerListeners() {
        log.info("Registering listeners");
        Set<Location> editedSpawners = ConcurrentHashMap.newKeySet();
        SilkSpawnersEventHandler eventHandler = new SilkSpawnersEventHandler(this);
        eventHandler.registerListener(new PlayerInteractListener(editedSpawners));
        eventHandler.registerListener(new BlockPlaceListener(editedSpawners));
        eventHandler.registerListener(new BlockBreakListener());
        eventHandler.registerListener(new SpawnerBreakListener());
    }

    private void registerCommands() {
        log.info("Registering commands");
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

    private void registerApiService() {
        log.info("Registering API service");
        new SilkSpawnersService(this).register();
    }

    private void registerHooks() {
        log.info("Registering hooks");
        HookLoader hookLoader = new HookLoader(this);
        hookLoader.addHook("shopguiplus.ShopGUIPlusHook", "ShopGUIPlus", PluginConfig.HOOK_SHOPGUIPLUS);
        hookLoader.register();
    }

    public synchronized boolean reloadConfiguration() {
        if (!loader.get(ConfigLoader.class).reload()) return false;
        versionChecker.restart();
        return true;
    }

    @Override
    public void onDisable() {
        if (versionChecker == null) return;
        log.info("Stopping version checker");
        versionChecker.stop();
    }

    public ServerPlatform getPlatform() {
        return loader.get(PlatformLoader.class).getServerPlatform();
    }

    public Bukkit getBukkitHandler() {
        return loader.get(CrossVersionHandler.class).getBukkitHandler();
    }

    public LocaleHandler getLocale() {
        return loader.get(LocaleHandler.class);
    }
}
