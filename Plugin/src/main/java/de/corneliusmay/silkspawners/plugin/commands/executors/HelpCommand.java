package de.corneliusmay.silkspawners.plugin.commands.executors;

import de.corneliusmay.silkspawners.plugin.SilkSpawners;
import de.corneliusmay.silkspawners.plugin.commands.SilkSpawnersCommand;
import org.bukkit.command.CommandSender;

public class HelpCommand extends SilkSpawnersCommand {

    public HelpCommand() {
        super("help", false);
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        sender.sendMessage(getMessage("MESSAGE", SilkSpawners.getInstance().getCommandHandler().getAvailableCommandsString(sender)));
        return false;
    }
}
