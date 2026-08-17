package de.corneliusmay.silkspawners.plugin;

import de.corneliusmay.silkspawners.plugin.commands.handler.SilkSpawnersCommand;
import de.corneliusmay.silkspawners.plugin.commands.handler.SilkSpawnersCommandHandler;
import de.corneliusmay.silkspawners.plugin.update.UpdateChecker;
import de.corneliusmay.silkspawners.plugin.utils.Logger;
import org.weftkit.wiring.Registry;
import org.weftkit.wiring.bukkit.WeftPlugin;
import org.weftkit.wiring.bukkit.metrics.WeftMetrics;
import org.weftkit.wiring.runtime.ComponentRegistry;
import org.weftkit.wiring.runtime.WeftLoader;

@Registry
@WeftMetrics(reportName = true)
public class SilkSpawners extends WeftPlugin {

    @Override
    protected ComponentRegistry registry() {
        return WeftWiring.INSTANCE;
    }

    @Override
    protected void onWeftEnable(WeftLoader loader) {
        registerCommands(loader);
        logEnabled(loader);
    }

    private void registerCommands(WeftLoader loader) {
        SilkSpawnersCommandHandler commandHandler = loader.create(SilkSpawnersCommandHandler.class, "silkspawners");
        loader.createAll(SilkSpawnersCommand.class).forEach(commandHandler::addCommand);
        commandHandler.register();
    }

    private void logEnabled(WeftLoader loader) {
        Logger logger = loader.get(Logger.class);
        UpdateChecker updateChecker = loader.get(UpdateChecker.class);
        logger.info("Enabled SilkSpawners v" + updateChecker.getInstalledVersion() + " in "
                + loader.totalLoadTime().toMillis() + "ms");
    }
}
