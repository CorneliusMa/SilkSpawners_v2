package de.corneliusmay.silkspawners.plugin.commands.handler;

import de.corneliusmay.silkspawners.plugin.SilkSpawners;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class SilkSpawnersCommand {

    @Setter(AccessLevel.PACKAGE)
    private SilkSpawnersCommandHandler commandHandler;

    @Setter(AccessLevel.PACKAGE)
    protected SilkSpawners plugin;

    @Getter(AccessLevel.PACKAGE)
    private final String command;

    @Getter(AccessLevel.PACKAGE)
    private final TabCompletion[] completions;

    private final boolean requiresPermission;

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
        return commandHandler.getMainCommand() + ".command." + command;
    }

    private String getMessage(String key, Object... args) {
        return plugin.getLocale().getMessage("COMMAND_" + commandHandler.getMainCommand().toUpperCase() + "_" + command.toUpperCase() + "_" + key, args);
    }

    protected final void sendMessage(CommandSender sender, String key, Object... args) {
        sender.sendMessage(getMessage(key, args));
    }

    protected final void sendMessage(Player player, String key, Object... args) {
        player.sendMessage(getMessage(key, args));
    }

    public boolean insufficientPermission(CommandSender sender) {
        sender.sendMessage(plugin.getLocale().getMessage("COMMAND_INSUFFICIENT_PERMISSIONS"));
        return false;
    }

    public boolean invalidSyntax(CommandSender sender) {
        sendMessage(sender, "USAGE");
        return false;
    }

    protected abstract boolean execute(CommandSender sender, String[] args);

}
