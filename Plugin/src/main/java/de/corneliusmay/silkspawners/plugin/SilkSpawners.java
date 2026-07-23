package de.corneliusmay.silkspawners.plugin;

import de.corneliusmay.silkspawners.plugin.api.SilkSpawnersService;
import de.corneliusmay.silkspawners.plugin.commands.handler.SilkSpawnersCommand;
import de.corneliusmay.silkspawners.plugin.commands.handler.SilkSpawnersCommandHandler;
import de.corneliusmay.silkspawners.plugin.config.ConfigLoader;
import de.corneliusmay.silkspawners.plugin.config.PluginConfig;
import de.corneliusmay.silkspawners.plugin.hooks.HookLoader;
import de.corneliusmay.silkspawners.plugin.loader.PluginLoader;
import de.corneliusmay.silkspawners.plugin.locale.LocaleHandler;
import de.corneliusmay.silkspawners.plugin.metrics.MetricsHandler;
import de.corneliusmay.silkspawners.plugin.utils.Logger;
import de.corneliusmay.silkspawners.plugin.version.VersionChecker;
import java.io.IOException;
import java.util.MissingResourceException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BooleanSupplier;
import org.bukkit.Location;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class SilkSpawners extends JavaPlugin {

    private PluginLoader loader;

    @Override
    public void onEnable() {
        loader = new PluginLoader(this);
        if (!loader.load()) return;

        registerListeners();
        registerCommands();
        registerApiService();
        registerHooks();

        Logger.info("Enabled SilkSpawners v" + loader.get(VersionChecker.class).getInstalledVersion());
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
        loader.createAll(SilkSpawnersCommand.class, (BooleanSupplier) this::reloadConfiguration)
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

    private synchronized boolean reloadConfiguration() {
        if (!loader.get(ConfigLoader.class).reload()) return false;
        try {
            LocaleHandler localeHandler = loader.get(LocaleHandler.class);
            if (!localeHandler.isSelectedLocaleLoaded()) localeHandler.loadLocale();
        } catch (IOException | MissingResourceException ex) {
            Logger.error("Error loading locale file", ex);
            return false;
        }
        loader.get(VersionChecker.class).restart();
        return true;
    }

    @Override
    public void onDisable() {
        if (loader == null) return;

        VersionChecker versionChecker = loader.get(VersionChecker.class);
        if (versionChecker != null) {
            Logger.info("Stopping version checker");
            versionChecker.stop();
        }

        MetricsHandler metricsHandler = loader.get(MetricsHandler.class);
        if (metricsHandler != null) metricsHandler.stop();
    }
}
