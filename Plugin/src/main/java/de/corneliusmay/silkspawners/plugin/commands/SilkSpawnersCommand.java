package de.corneliusmay.silkspawners.plugin.commands;

import de.corneliusmay.silkspawners.plugin.SilkSpawners;
import lombok.Getter;
import org.bukkit.command.CommandSender;

public abstract class SilkSpawnersCommand {

    @Getter
    private final String command;

    private final boolean requiresPermission;

    @Getter
    private final TabCompletion[] completions;

    public SilkSpawnersCommand(String command, Boolean requiresPermission, TabCompletion... completions) {
        this.command = command;
        this.requiresPermission = requiresPermission;
        this.completions = completions;
    }

    public final boolean hasPermission(CommandSender sender) {
        if(!requiresPermission) return true;
        return sender.hasPermission("silkspawners.command." + command);
    }

    public boolean insufficientPermission(CommandSender sender) {
        sender.sendMessage(SilkSpawners.getInstance().getPluginConfig().getPrefix() + "§f" + SilkSpawners.getInstance().getPluginConfig().getInsufficientPermissionMessage());
        return false;
    }

    public abstract boolean execute(CommandSender sender, String[] args);

}
