package de.corneliusmay.silkspawners.plugin.commands;

import de.corneliusmay.silkspawners.plugin.commands.completers.ConfigKeyTabCompleter;
import de.corneliusmay.silkspawners.plugin.commands.completers.ConfigValueTabCompleter;
import de.corneliusmay.silkspawners.plugin.commands.handler.SilkSpawnersCommand;
import de.corneliusmay.silkspawners.plugin.commands.handler.StaticTabCompletion;
import de.corneliusmay.silkspawners.plugin.config.ConfigApply;
import de.corneliusmay.silkspawners.plugin.config.ConfigEditor;
import de.corneliusmay.silkspawners.plugin.config.ConfigKey;
import de.corneliusmay.silkspawners.plugin.config.handler.ConfigValueException;
import de.corneliusmay.silkspawners.wiring.Wired;
import java.io.IOException;
import java.util.Arrays;
import java.util.function.BooleanSupplier;
import org.bukkit.command.CommandSender;

@Wired
public class ConfigCommand extends SilkSpawnersCommand {

    private final ConfigEditor editor;

    private final BooleanSupplier configReloader;

    public ConfigCommand(ConfigEditor editor, BooleanSupplier configReloader) {
        super(
                "config",
                true,
                new StaticTabCompletion("reload", "get", "set"),
                new ConfigKeyTabCompleter(editor),
                new ConfigValueTabCompleter(editor));
        this.editor = editor;
        this.configReloader = configReloader;
    }

    public static boolean canSet(SilkSpawnersCommand command, CommandSender sender) {
        return sender.hasPermission(command.getPermissionString() + ".set");
    }

    @Override
    protected boolean execute(CommandSender sender, String[] args) {
        if (args.length == 0) return invalidSyntax(sender);

        return switch (args[0].toLowerCase()) {
            case "reload" -> args.length == 1 ? reload(sender) : invalidSyntax(sender);
            case "get" -> args.length == 2 ? get(sender, args[1]) : fail(sender, "GET_USAGE");
            case "set" ->
                args.length >= 3
                        ? set(sender, args[1], String.join(" ", Arrays.copyOfRange(args, 2, args.length)))
                        : fail(sender, "SET_USAGE");
            default -> fail(sender, "COMMAND_NOT_FOUND", args[0]);
        };
    }

    private boolean reload(CommandSender sender) {
        if (configReloader.getAsBoolean()) sendMessage(sender, "RELOAD_SUCCESSFUL");
        else sendMessage(sender, "RELOAD_ERROR");
        return true;
    }

    private boolean get(CommandSender sender, String path) {
        ConfigKey<?> key = key(sender, path);
        if (key == null) return false;

        sendMessage(sender, "GET_VALUE", key.getPath(), editor.currentValue(key), editor.description(key));
        return true;
    }

    private boolean set(CommandSender sender, String path, String input) {
        if (!canSet(this, sender)) return insufficientPermission(sender);

        ConfigKey<?> key = key(sender, path);
        if (key == null) return false;

        String value = editor.allowedValue(key, input);
        if (value == null) return fail(sender, "SET_UNKNOWN_VALUE", key.getPath(), input, values(key));

        try {
            editor.set(key, value);
        } catch (IOException | ConfigValueException ex) {
            return fail(sender, "SET_ERROR", key.getPath(), value, ex.getMessage());
        }

        if (key.getApply() == ConfigApply.AFTER_RELOAD && !configReloader.getAsBoolean())
            return fail(sender, "SET_RELOAD_ERROR", key.getPath(), value);

        sendMessage(sender, "SET_SUCCESSFUL", key.getPath(), value);
        if (key.getApply() == ConfigApply.AFTER_RESTART) sendMessage(sender, "SET_RESTART_REQUIRED", key.getPath());
        return true;
    }

    private ConfigKey<?> key(CommandSender sender, String path) {
        ConfigKey<?> key = editor.find(path);
        if (key == null) {
            sendMessage(sender, "KEY_NOT_FOUND", path);
            return null;
        }
        if (!key.isSettable()) {
            sendMessage(sender, "NOT_SETTABLE", key.getPath());
            return null;
        }
        return key;
    }

    private String values(ConfigKey<?> key) {
        return String.join(", ", editor.allowedValues(key));
    }

    private boolean fail(CommandSender sender, String key, Object... args) {
        sendMessage(sender, key, args);
        return false;
    }
}
