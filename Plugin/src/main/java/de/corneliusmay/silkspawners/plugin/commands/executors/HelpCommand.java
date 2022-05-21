package de.corneliusmay.silkspawners.plugin.commands.executors;

import de.corneliusmay.silkspawners.plugin.SilkSpawners;
import de.corneliusmay.silkspawners.plugin.commands.SilkSpawnersCommand;
import org.bukkit.command.CommandSender;

public class HelpCommand extends SilkSpawnersCommand {

    public HelpCommand() {
        super("help", false, (sender) -> SilkSpawners.getInstance().getCommandHandler().getCommands(sender));
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        switch (args.length) {
            case 0 -> sender.sendMessage(getMessage("MESSAGE", SilkSpawners.getInstance().getCommandHandler().getAvailableCommandsString(sender)));
            case 1 -> {
                SilkSpawnersCommand command = SilkSpawners.getInstance().getCommandHandler().getCommand(args[0]);
                if(command != null) sender.sendMessage(getMessage("MESSAGE_" + command.getCommand().toUpperCase()));
                else sender.sendMessage(getMessage("COMMAND_NOT_FOUND", args[0]));
            }
            default -> invalidSyntax(sender);
        }
        return true;
    }
}
