package de.corneliusmay.silkspawners.plugin.commands.handler;

import org.bukkit.command.CommandSender;

class HelpCommand extends SilkSpawnersCommand {

    private final SilkSpawnersCommandHandler commandHandler;

    public HelpCommand(SilkSpawnersCommandHandler commandHandler) {
        super("help", false, (command, sender) -> commandHandler.getCommands(sender));
        this.commandHandler = commandHandler;
    }

    @Override
    protected boolean execute(CommandSender sender, String[] args) {
        switch (args.length) {
            case 0 -> sendMessage(sender, "MESSAGE", commandHandler.getAvailableCommandsString(sender));
            case 1 -> {
                SilkSpawnersCommand command = commandHandler.getCommand(args[0]);
                if(command != null) sendMessage(sender, "MESSAGE_" + command.getCommand().toUpperCase());
                else sendMessage(sender, "COMMAND_NOT_FOUND", args[0]);
            }
            default -> invalidSyntax(sender);
        }
        return true;
    }
}
