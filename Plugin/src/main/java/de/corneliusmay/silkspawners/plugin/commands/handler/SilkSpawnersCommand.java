package de.corneliusmay.silkspawners.plugin.commands.handler;

import de.corneliusmay.silkspawners.plugin.locale.LocaleHandler;
import de.corneliusmay.silkspawners.plugin.message.InteractiveMessages;
import de.corneliusmay.silkspawners.spi.message.ClickAction;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class SilkSpawnersCommand {

    @Setter(AccessLevel.PACKAGE)
    private SilkSpawnersCommandHandler commandHandler;

    @Setter(AccessLevel.PACKAGE)
    private LocaleHandler locale;

    @Setter(AccessLevel.PACKAGE)
    private InteractiveMessages interactive;

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
        if (!requiresPermission) return true;
        return sender.hasPermission(getPermissionString());
    }

    public final boolean hasSubPermission(CommandSender sender, String action) {
        return sender.hasPermission(getPermissionString() + "." + action);
    }

    public final String getPermissionString() {
        return commandHandler.getMainCommand() + ".command." + command;
    }

    protected final String commandString(String... args) {
        String suffix = args.length == 0 ? "" : " " + String.join(" ", args);
        return "/" + commandHandler.getMainCommand() + " " + command + suffix;
    }

    protected final String getMessage(String key, Object... args) {
        return locale.getMessage(messageKey(key), args);
    }

    private String messageKey(String key) {
        return "COMMAND_" + commandHandler.getMainCommand().toUpperCase() + "_" + command.toUpperCase() + "_" + key;
    }

    protected final void sendMessage(CommandSender sender, String key, Object... args) {
        sender.sendMessage(getMessage(key, args));
    }

    protected final void sendInteractive(CommandSender sender, ClickAction action, String key, Object... args) {
        interactive.send(sender, action, messageKey(key), args);
    }

    protected final void sendMessage(Player player, String key, Object... args) {
        player.sendMessage(getMessage(key, args));
    }

    public boolean insufficientPermission(CommandSender sender) {
        sender.sendMessage(locale.getMessage("COMMAND_INSUFFICIENT_PERMISSIONS"));
        return false;
    }

    public boolean invalidSyntax(CommandSender sender) {
        sendMessage(sender, "USAGE");
        return false;
    }

    protected abstract boolean execute(CommandSender sender, String[] args);
}
