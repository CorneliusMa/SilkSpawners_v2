package de.corneliusmay.silkspawners.plugin;

import de.corneliusmay.silkspawners.plugin.api.SilkSpawnersService;
import de.corneliusmay.silkspawners.plugin.commands.handler.SilkSpawnersCommand;
import de.corneliusmay.silkspawners.plugin.commands.handler.SilkSpawnersCommandHandler;
import de.corneliusmay.silkspawners.plugin.config.ConfigLoader;
import de.corneliusmay.silkspawners.plugin.config.PluginConfig;
import de.corneliusmay.silkspawners.plugin.hooks.HookLoader;
import de.corneliusmay.silkspawners.plugin.loader.PluginLoader;
import de.corneliusmay.silkspawners.plugin.utils.Logger;
import de.corneliusmay.silkspawners.plugin.version.VersionChecker;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BooleanSupplier;
import org.bstats.bukkit.Metrics;
import org.bukkit.Location;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class SilkSpawners extends JavaPlugin {

    private VersionChecker versionChecker;

    private PluginLoader loader;

    @Override
    public void onEnable() {
        loader = new PluginLoader(this);
        versionChecker = loader.create(VersionChecker.class, getDescription().getVersion());
        if (!loader.load()) return;

        registerListeners();
        registerCommands();
        registerApiService();
        registerHooks();
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

    private void registerListeners() {
        Logger.info("Registering listeners");
        Set<Location> editedSpawners = ConcurrentHashMap.newKeySet();
        PluginManager pluginManager = getServer().getPluginManager();
        loader.createAll(Listener.class, editedSpawners)
                .forEach(listener -> pluginManager.registerEvents(listener, this));
    }

    private void registerCommands() {
        Logger.info("Registering commands");
        SilkSpawnersCommandHandler commandHandler = loader.create(SilkSpawnersCommandHandler.class, "silkspawners");
        loader.createAll(SilkSpawnersCommand.class, versionChecker, (BooleanSupplier) this::reloadConfiguration)
                .forEach(commandHandler::addCommand);
        commandHandler.register();
    }

    private void registerApiService() {
        Logger.info("Registering API service");
        loader.create(SilkSpawnersService.class).register();
    }

    private void registerHooks() {
        Logger.info("Registering hooks");
        HookLoader hookLoader = loader.create(HookLoader.class);
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
