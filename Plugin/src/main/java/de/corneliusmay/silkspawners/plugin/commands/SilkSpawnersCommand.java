package de.corneliusmay.silkspawners.plugin.commands;

import de.corneliusmay.silkspawners.plugin.SilkSpawners;
import lombok.Getter;
import org.bukkit.command.CommandSender;

public abstract class SilkSpawnersCommand {

    @Getter
    private final String command;

    @Getter
    private final TabCompletion[] completions;

    public SilkSpawnersCommand(String command, TabCompletion... completions) {
        this.command = command;
        this.completions = completions;
    }

    public void insufficientPermission(CommandSender sender) {
        sender.sendMessage(SilkSpawners.getInstance().getPluginConfig().getPrefix() + "§f" + SilkSpawners.getInstance().getPluginConfig().getInsufficientPermissionMessage());
    }

    public abstract boolean execute(CommandSender sender, String[] args);

}
