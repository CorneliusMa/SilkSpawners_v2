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
        if(!SilkSpawners.getInstance().getPluginConfig().checkForUpdates()) {
            sender.sendMessage(SilkSpawners.getInstance().getPluginConfig().getPrefix() + " §cUpdate checking is disabled. §7\n§7The currently installed version is v" + VersionChecker.getInstalledVersion());
            return false;
        }

        sender.sendMessage(SilkSpawners.getInstance().getPluginConfig().getPrefix() +
                (SilkSpawners.getInstance().getVersionChecker().check()? " §aYou are up to date" : " §eAn update is available!") +
                "§7\n§7The currently installed version is v" + VersionChecker.getInstalledVersion() +
                (SilkSpawners.getInstance().getVersionChecker().check()? "" : "\n§7The latest version is v" + SilkSpawners.getInstance().getVersionChecker().getLatestVersion()));
        return true;
    }
}
