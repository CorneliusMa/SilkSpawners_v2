package de.corneliusmay.silkspawners.plugin.commands.executors;

import de.corneliusmay.silkspawners.plugin.SilkSpawners;
import de.corneliusmay.silkspawners.plugin.commands.SilkSpawnersCommand;
import de.corneliusmay.silkspawners.plugin.commands.StaticTabCompletion;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;

import java.util.Arrays;

public class PermissionsCommand extends SilkSpawnersCommand {

    public PermissionsCommand() {
        super("permissions", true, new StaticTabCompletion("commands", "spawners", "all"));
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if(args.length != 1) return invalidSyntax(sender);

        switch (args[0].toLowerCase()) {
            case "commands", "c" -> sender.sendMessage(getCommandPermissions());
            case "spawners", "s" -> sender.sendMessage(getSpawnerPermissions());
            case "all", "a" -> sender.sendMessage(getAllPermissions());
            default -> invalidSyntax(sender);
        }
        return true;
    }

    private String getAllPermissions() {
        return getCommandPermissions() + "\n" + getSpawnerPermissions();
    }

    private String getCommandPermissions() {
        StringBuilder msg = new StringBuilder("Command Permissions: ");
        for(SilkSpawnersCommand command : SilkSpawners.getInstance().getCommandHandler().getCommands()) {
            msg.append("\n - silkspawners.command.").append(command.getCommand());
        }

        msg.append("\n§f");
        return msg.toString();
    }

    private String getSpawnerPermissions() {
        StringBuilder msg = new StringBuilder("Spawner Permissions: ");
        msg.append("\n - silkspawners.place.<entity>\n - silkspawners.break.<entity>\n - silkspawners.change.<entity>\nYou can use the following entities: \n");
        for(EntityType entity : Arrays.stream(EntityType.values()).filter(EntityType::isSpawnable).toList()) {
            msg.append(entity.getName()).append(", ");
        }

        msg.delete(msg.length() - 2, msg.length() - 1);
        msg.append("\n§f");
        return msg.toString();
    }
}
