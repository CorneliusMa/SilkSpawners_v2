package de.corneliusmay.silkspawners.plugin.commands;

import de.corneliusmay.silkspawners.plugin.commands.handler.SilkSpawnersCommand;
import de.corneliusmay.silkspawners.plugin.config.handler.ConfigValue;
import de.corneliusmay.silkspawners.plugin.config.PluginConfig;
import org.bukkit.command.CommandSender;

public class VersionCommand extends SilkSpawnersCommand {

    public VersionCommand() {
        super("version", true);
    }

    @Override
    protected boolean execute(CommandSender sender, String[] args) {
        if(args.length != 0) return invalidSyntax(sender);

        if(!new ConfigValue<Boolean>(PluginConfig.UPDATE_CHECK_ENABLED).get()) {
            sendMessage(sender, "ERROR", plugin.getVersionChecker().getInstalledVersion());
            return false;
        }

        if(plugin.getVersionChecker().check()) sendMessage(sender, "INFO", plugin.getVersionChecker().getInstalledVersion());
        else sendMessage(sender, "UPDATE_AVAILABLE", plugin.getVersionChecker().getInstalledVersion(), plugin.getVersionChecker().getLatestVersion());
        return true;
    }
}
