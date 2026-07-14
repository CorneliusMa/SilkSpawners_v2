package de.corneliusmay.silkspawners.plugin;

import de.corneliusmay.silkspawners.plugin.api.SilkSpawnersService;
import de.corneliusmay.silkspawners.plugin.commands.ConfigCommand;
import de.corneliusmay.silkspawners.plugin.commands.EntitiesCommand;
import de.corneliusmay.silkspawners.plugin.commands.ExplosionCommand;
import de.corneliusmay.silkspawners.plugin.commands.GiveCommand;
import de.corneliusmay.silkspawners.plugin.commands.LocaleCommand;
import de.corneliusmay.silkspawners.plugin.commands.SetCommand;
import de.corneliusmay.silkspawners.plugin.commands.VersionCommand;
import de.corneliusmay.silkspawners.plugin.commands.handler.SilkSpawnersCommandHandler;
import de.corneliusmay.silkspawners.plugin.config.ConfigLoader;
import de.corneliusmay.silkspawners.plugin.config.PluginConfig;
import de.corneliusmay.silkspawners.plugin.hooks.HookLoader;
import de.corneliusmay.silkspawners.plugin.listeners.BlockBreakListener;
import de.corneliusmay.silkspawners.plugin.listeners.BlockPlaceListener;
import de.corneliusmay.silkspawners.plugin.listeners.PlayerInteractListener;
import de.corneliusmay.silkspawners.plugin.listeners.SpawnerBreakListener;
import de.corneliusmay.silkspawners.plugin.loader.PluginLoader;
import de.corneliusmay.silkspawners.plugin.locale.LocaleHandler;
import de.corneliusmay.silkspawners.plugin.platform.PlatformLoader;
import de.corneliusmay.silkspawners.plugin.spawner.SilkDropCheck;
import de.corneliusmay.silkspawners.plugin.spawner.SpawnerFactory;
import de.corneliusmay.silkspawners.plugin.utils.Logger;
import de.corneliusmay.silkspawners.plugin.version.CrossVersionHandler;
import de.corneliusmay.silkspawners.plugin.version.VersionChecker;
import de.corneliusmay.silkspawners.spi.platform.ServerPlatform;
import de.corneliusmay.silkspawners.spi.version.Bukkit;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.bstats.bukkit.Metrics;
import org.bukkit.Location;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class SilkSpawners extends JavaPlugin {

    private VersionChecker versionChecker;

    private PluginLoader loader;

    @Override
    public void onEnable() {
        loader = new PluginLoader(this);
        versionChecker = new VersionChecker(getDescription().getVersion());

        ConfigLoader config = new ConfigLoader(this);
        PlatformLoader platformLoader = new PlatformLoader(this);
        CrossVersionHandler versionHandler = new CrossVersionHandler();
        if (!loader.load(config, platformLoader, versionHandler, new LocaleHandler(this, config))) return;

        ServerPlatform platform = platformLoader.getServerPlatform();
        Bukkit bukkitHandler = versionHandler.getBukkitHandler();
        SpawnerFactory spawnerFactory = new SpawnerFactory(bukkitHandler, platform);
        registerListeners(spawnerFactory, bukkitHandler, platform);
        registerCommands(spawnerFactory, bukkitHandler, platform);
        registerApiService(spawnerFactory, bukkitHandler);
        registerHooks(spawnerFactory);
        startOptional(versionChecker::start);
        startOptional(this::startMetrics);

        Logger.info("Enabled SilkSpawners v" + versionChecker.getInstalledVersion());
    }

    private void startOptional(Runnable step) {
        try {
            step.run();
        } catch (RuntimeException ex) {
            Logger.error("An optional startup step threw and was skipped", ex);
        }
    }

    private void startMetrics() {
        Logger.info("Starting bStats integration");
        new Metrics(this, 15215);
    }

    private void registerListeners(SpawnerFactory spawnerFactory, Bukkit bukkitHandler, ServerPlatform platform) {
        Logger.info("Registering listeners");
        Set<Location> editedSpawners = ConcurrentHashMap.newKeySet();
        LocaleHandler locale = loader.get(LocaleHandler.class);
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(
                new PlayerInteractListener(spawnerFactory, locale, platform, editedSpawners), this);
        pluginManager.registerEvents(
                new BlockPlaceListener(spawnerFactory, bukkitHandler, locale, editedSpawners), this);
        pluginManager.registerEvents(
                new BlockBreakListener(spawnerFactory, new SilkDropCheck(bukkitHandler), locale, platform), this);
        pluginManager.registerEvents(new SpawnerBreakListener(platform), this);
    }

    private void registerCommands(SpawnerFactory spawnerFactory, Bukkit bukkitHandler, ServerPlatform platform) {
        Logger.info("Registering commands");
        SilkSpawnersCommandHandler commandHandler =
                new SilkSpawnersCommandHandler(this, loader.get(LocaleHandler.class), "silkspawners");
        commandHandler.addCommand(new GiveCommand(spawnerFactory, platform));
        commandHandler.addCommand(new SetCommand(spawnerFactory, bukkitHandler));
        commandHandler.addCommand(new ExplosionCommand(this, platform));
        commandHandler.addCommand(new VersionCommand(versionChecker));
        commandHandler.addCommand(new LocaleCommand(loader.get(LocaleHandler.class)));
        commandHandler.addCommand(new ConfigCommand(this::reloadConfiguration));
        commandHandler.addCommand(new EntitiesCommand());
        commandHandler.register();
    }

    private void registerApiService(SpawnerFactory spawnerFactory, Bukkit bukkitHandler) {
        Logger.info("Registering API service");
        new SilkSpawnersService(this, spawnerFactory, bukkitHandler, new SilkDropCheck(bukkitHandler)).register();
    }

    private void registerHooks(SpawnerFactory spawnerFactory) {
        Logger.info("Registering hooks");
        HookLoader hookLoader = new HookLoader(this, spawnerFactory);
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
        Logger.info("Stopping version checker");
        versionChecker.stop();
    }
}
