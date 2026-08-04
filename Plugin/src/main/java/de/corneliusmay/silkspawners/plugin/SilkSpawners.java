package de.corneliusmay.silkspawners.plugin;

import de.corneliusmay.silkspawners.plugin.api.SilkSpawnersService;
import de.corneliusmay.silkspawners.plugin.commands.handler.SilkSpawnersCommand;
import de.corneliusmay.silkspawners.plugin.commands.handler.SilkSpawnersCommandHandler;
import de.corneliusmay.silkspawners.plugin.config.ConfigLoader;
import de.corneliusmay.silkspawners.plugin.config.PluginConfig;
import de.corneliusmay.silkspawners.plugin.hooks.HookLoader;
import de.corneliusmay.silkspawners.plugin.locale.LocaleHandler;
import de.corneliusmay.silkspawners.plugin.utils.Logger;
import de.corneliusmay.silkspawners.plugin.version.VersionChecker;
import java.io.IOException;
import java.time.Duration;
import java.util.MissingResourceException;
import java.util.function.BooleanSupplier;
import org.bukkit.plugin.java.JavaPlugin;
import org.weftkit.wiring.Registry;
import org.weftkit.wiring.bukkit.BukkitWeft;
import org.weftkit.wiring.runtime.WeftLoader;

@Registry
public class SilkSpawners extends JavaPlugin {

    private WeftLoader loader;

    @Override
    public void onEnable() {
        loader = BukkitWeft.enable(this, WiredComponents.INSTANCE);
        if (loader == null) return;

        registerCommands();
        registerApiService();
        registerHooks();

        Logger.info("Enabled SilkSpawners v" + loader.get(VersionChecker.class).getInstalledVersion() + " ("
                + loadSummary() + ")");
    }

    private String loadSummary() {
        long millis = loader.loadTimings().values().stream()
                .mapToLong(Duration::toMillis)
                .sum();
        return loader.loadTimings().size() + " components in " + millis + "ms";
    }

    private void registerCommands() {
        SilkSpawnersCommandHandler commandHandler = loader.create(SilkSpawnersCommandHandler.class, "silkspawners");
        loader.createAll(SilkSpawnersCommand.class, (BooleanSupplier) this::reloadConfiguration)
                .forEach(commandHandler::addCommand);
        commandHandler.register();
    }

    private void registerApiService() {
        loader.create(SilkSpawnersService.class).register();
    }

    private void registerHooks() {
        HookLoader hookLoader = loader.get(HookLoader.class);
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
        BukkitWeft.disable(this, loader);
    }
}
