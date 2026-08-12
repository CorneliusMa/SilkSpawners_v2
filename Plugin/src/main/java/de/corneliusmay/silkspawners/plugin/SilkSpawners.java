package de.corneliusmay.silkspawners.plugin;

import de.corneliusmay.silkspawners.plugin.commands.handler.SilkSpawnersCommand;
import de.corneliusmay.silkspawners.plugin.commands.handler.SilkSpawnersCommandHandler;
import de.corneliusmay.silkspawners.plugin.config.PluginConfig;
import de.corneliusmay.silkspawners.plugin.hooks.HookLoader;
import de.corneliusmay.silkspawners.plugin.utils.Logger;
import de.corneliusmay.silkspawners.plugin.version.VersionChecker;
import org.weftkit.wiring.Registry;
import org.weftkit.wiring.bukkit.WeftPlugin;
import org.weftkit.wiring.runtime.ComponentRegistry;
import org.weftkit.wiring.runtime.WeftLoader;

@Registry
public class SilkSpawners extends WeftPlugin {

    @Override
    protected ComponentRegistry registry() {
        return WeftWiring.INSTANCE;
    }

    @Override
    protected void onWeftEnable(WeftLoader loader) {
        registerCommands(loader);
        registerHooks(loader);
        logEnabled(loader);
    }

    private void registerCommands(WeftLoader loader) {
        SilkSpawnersCommandHandler commandHandler = loader.create(SilkSpawnersCommandHandler.class, "silkspawners");
        loader.createAll(SilkSpawnersCommand.class).forEach(commandHandler::addCommand);
        commandHandler.register();
    }

    private void registerHooks(WeftLoader loader) {
        HookLoader hookLoader = loader.get(HookLoader.class);
        hookLoader.addHook("shopguiplus.ShopGUIPlusHook", "ShopGUIPlus", PluginConfig.HOOK_SHOPGUIPLUS);
        hookLoader.register();
    }

    private void logEnabled(WeftLoader loader) {
        Logger logger = loader.get(Logger.class);
        VersionChecker versionChecker = loader.get(VersionChecker.class);
        logger.info("Enabled SilkSpawners v" + versionChecker.getInstalledVersion() + " in "
                + loader.totalLoadTime().toMillis() + "ms");
    }
}
