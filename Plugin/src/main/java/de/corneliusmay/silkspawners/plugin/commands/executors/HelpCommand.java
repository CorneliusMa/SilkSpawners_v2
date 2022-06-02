package de.corneliusmay.silkspawners.plugin.commands.executors;

import de.corneliusmay.silkspawners.plugin.commands.SilkSpawnersCommand;
import de.corneliusmay.silkspawners.plugin.commands.SilkSpawnersCommandHandler;
import org.bukkit.command.CommandSender;

public class HelpCommand extends SilkSpawnersCommand {

    public HelpCommand(SilkSpawnersCommandHandler commandHandler) {
        super("help", false, (command, sender) -> commandHandler.getCommands(sender));
    }

    @Override
    protected boolean execute(CommandSender sender, String[] args) {
        switch (args.length) {
            case 0 -> sender.sendMessage(getMessage("MESSAGE", plugin.getCommandHandler().getAvailableCommandsString(sender)));
            case 1 -> {
                SilkSpawnersCommand command = plugin.getCommandHandler().getCommand(args[0]);
                if(command != null) sender.sendMessage(getMessage("MESSAGE_" + command.getCommand().toUpperCase()));
                else sender.sendMessage(getMessage("COMMAND_NOT_FOUND", args[0]));
            }
            default -> invalidSyntax(sender);
        }
        return true;
    }
}
