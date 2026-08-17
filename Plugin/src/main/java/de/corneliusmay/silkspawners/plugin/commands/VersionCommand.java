package de.corneliusmay.silkspawners.plugin.commands;

import de.corneliusmay.silkspawners.plugin.commands.handler.SilkSpawnersCommand;
import de.corneliusmay.silkspawners.plugin.config.PluginConfig;
import de.corneliusmay.silkspawners.plugin.update.UpdateChecker;
import de.corneliusmay.silkspawners.spi.message.ClickAction;
import java.util.Optional;
import org.bukkit.command.CommandSender;
import org.weftkit.wiring.Wired;

@Wired
class VersionCommand extends SilkSpawnersCommand {

    private final UpdateChecker updateChecker;

    private final PluginConfig config;

    VersionCommand(UpdateChecker updateChecker, PluginConfig config) {
        super("version", true);
        this.updateChecker = updateChecker;
        this.config = config;
    }

    @Override
    protected boolean execute(CommandSender sender, String[] args) {
        if (args.length != 0) return invalidSyntax(sender);

        String installedVersion = updateChecker.getInstalledVersion();
        if (!config.UPDATE_CHECK_ENABLED.get()) {
            sendMessage(sender, "ERROR", installedVersion);
            return false;
        }

        Optional<String> update = updateChecker.getAvailableUpdate();
        if (update.isPresent())
            sendInteractive(
                    sender,
                    ClickAction.openUrl(UpdateChecker.DOWNLOAD_URL),
                    "UPDATE_AVAILABLE",
                    installedVersion,
                    update.get());
        else sendMessage(sender, "INFO", installedVersion);
        return true;
    }
}
