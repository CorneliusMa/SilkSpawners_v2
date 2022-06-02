package de.corneliusmay.silkspawners.plugin.commands;

import de.corneliusmay.silkspawners.plugin.SilkSpawners;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.command.CommandSender;

public abstract class SilkSpawnersCommand {

    @Setter(AccessLevel.PACKAGE)
    protected SilkSpawners plugin;

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
        return sender.hasPermission(getPermissionString());
    }

    public final String getPermissionString() {
        return "silkspawners.command." + command;
    }

    protected final String getMessage(String key, Object... args) {
        return plugin.getLocale().getMessage("COMMAND_" + command.toUpperCase() + "_" + key, args);
    }

    public boolean insufficientPermission(CommandSender sender) {
        sender.sendMessage(plugin.getLocale().getMessage("COMMAND_INSUFFICIENT_PERMISSIONS"));
        return false;
    }

    public boolean invalidSyntax(CommandSender sender) {
        sender.sendMessage(getMessage("USAGE"));
        return false;
    }

    protected abstract boolean execute(CommandSender sender, String[] args);

}
