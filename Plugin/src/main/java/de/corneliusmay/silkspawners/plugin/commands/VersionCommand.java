package de.corneliusmay.silkspawners.plugin.commands;

import de.corneliusmay.silkspawners.plugin.commands.handler.SilkSpawnersCommand;
import de.corneliusmay.silkspawners.plugin.config.PluginConfig;
import de.corneliusmay.silkspawners.plugin.version.VersionChecker;
import de.corneliusmay.silkspawners.wiring.Wired;
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

        if (!PluginConfig.UPDATE_CHECK_ENABLED.get()) {
            sendMessage(sender, "ERROR", versionChecker.getInstalledVersion());
            return false;
        }

        String latestVersion = versionChecker.getLatestVersion();
        if (versionChecker.check(latestVersion)) sendMessage(sender, "INFO", versionChecker.getInstalledVersion());
        else sendMessage(sender, "UPDATE_AVAILABLE", versionChecker.getInstalledVersion(), latestVersion);
        return true;
    }
}
