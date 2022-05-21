package de.corneliusmay.silkspawners.plugin.commands.executors;

import de.corneliusmay.silkspawners.plugin.SilkSpawners;
import de.corneliusmay.silkspawners.plugin.commands.SilkSpawnersCommand;
import de.corneliusmay.silkspawners.plugin.version.VersionChecker;
import org.bukkit.command.CommandSender;

public class VersionCommand extends SilkSpawnersCommand {

    public VersionCommand() {
        super("version", true);
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if(args.length != 0) return invalidSyntax(sender);

        if(!SilkSpawners.getInstance().getPluginConfig().checkForUpdates()) {
            sender.sendMessage(getMessage("ERROR", VersionChecker.getInstalledVersion()));
            return false;
        }

        if(SilkSpawners.getInstance().getVersionChecker().check()) sender.sendMessage(getMessage("INFO", VersionChecker.getInstalledVersion()));
        else sender.sendMessage(getMessage("UPDATE_AVAILABLE", VersionChecker.getInstalledVersion(), SilkSpawners.getInstance().getVersionChecker().getLatestVersion()));
        return true;
    }
}
