package de.corneliusmay.silkspawners.plugin.commands.executors;

import de.corneliusmay.silkspawners.plugin.SilkSpawners;
import de.corneliusmay.silkspawners.plugin.commands.SilkSpawnersCommand;
import org.bukkit.command.CommandSender;

public class HelpCommand extends SilkSpawnersCommand {

    public HelpCommand() {
        super("help");
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        sender.sendMessage(SilkSpawners.getInstance().getPluginConfig().getPrefix() + " §7You can execute the following commands: \n" + SilkSpawners.getInstance().getCommandHandler().getAvailableCommandsString(sender));
        return false;
    }
}
