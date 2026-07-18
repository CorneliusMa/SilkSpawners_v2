package de.corneliusmay.silkspawners.plugin.commands;

import de.corneliusmay.silkspawners.plugin.commands.handler.SilkSpawnersCommand;
import de.corneliusmay.silkspawners.plugin.config.PluginConfig;
import de.corneliusmay.silkspawners.plugin.version.VersionChecker;
import de.corneliusmay.silkspawners.wiring.Wired;
import java.util.Optional;
import org.bukkit.command.CommandSender;

@Wired
public class VersionCommand extends SilkSpawnersCommand {

    private final VersionChecker versionChecker;

    public VersionCommand(VersionChecker versionChecker) {
        super("version", true);
        this.versionChecker = versionChecker;
    }

    @Override
    protected boolean execute(CommandSender sender, String[] args) {
        if (args.length != 0) return invalidSyntax(sender);

        String installedVersion = versionChecker.getInstalledVersion();
        if (!PluginConfig.UPDATE_CHECK_ENABLED.get()) {
            sendMessage(sender, "ERROR", installedVersion);
            return false;
        }

        Optional<String> update = versionChecker.getAvailableUpdate();
        if (update.isPresent()) sendMessage(sender, "UPDATE_AVAILABLE", installedVersion, update.get());
        else sendMessage(sender, "INFO", installedVersion);
        return true;
    }
}
