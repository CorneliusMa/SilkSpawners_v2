package de.corneliusmay.silkspawners.plugin.commands;

import de.corneliusmay.silkspawners.plugin.commands.handler.SilkSpawnersCommand;
import de.corneliusmay.silkspawners.plugin.commands.handler.StaticTabCompletion;
import org.bukkit.command.CommandSender;

public class ConfigCommand extends SilkSpawnersCommand {

    public ConfigCommand() {
        super("config", true, new StaticTabCompletion("reload"));
    }

    @Override
    protected boolean execute(CommandSender sender, String[] args) {
        if (args.length != 1 || !args[0].equalsIgnoreCase("reload")) return invalidSyntax(sender);

        if (plugin.getConfigLoader().reload()) sendMessage(sender, "RELOAD_SUCCESSFUL");
        else sendMessage(sender, "RELOAD_ERROR");
        return true;
    }
}
