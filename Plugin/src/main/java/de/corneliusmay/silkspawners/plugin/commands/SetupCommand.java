package de.corneliusmay.silkspawners.plugin.commands;

import de.corneliusmay.silkspawners.plugin.commands.handler.SilkSpawnersCommand;
import de.corneliusmay.silkspawners.plugin.commands.handler.StaticTabCompletion;
import de.corneliusmay.silkspawners.plugin.config.ConfigEditor;
import de.corneliusmay.silkspawners.plugin.config.ConfigKey;
import de.corneliusmay.silkspawners.plugin.config.PluginConfig;
import de.corneliusmay.silkspawners.plugin.config.handler.ConfigValueException;
import de.corneliusmay.silkspawners.spi.message.ClickAction;
import java.io.IOException;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.weftkit.wiring.Wired;

@Wired
class SetupCommand extends SilkSpawnersCommand {

    private final List<ConfigKey<Boolean>> permissionKeys;

    private final ConfigEditor editor;

    SetupCommand(ConfigEditor editor, PluginConfig config) {
        super("setup", true, new StaticTabCompletion("revert"));
        this.editor = editor;
        this.permissionKeys = List.of(
                config.SPAWNER_PERMISSION_DISABLE_DESTROY,
                config.SPAWNER_PERMISSION_DISABLE_PLACE,
                config.SPAWNER_PERMISSION_DISABLE_CHANGE);
    }

    @Override
    protected boolean execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sendInteractive(sender, ClickAction.runCommand(commandString("confirm")), "WARNING");
            return true;
        }
        if (args.length != 1) return invalidSyntax(sender);

        boolean confirm = args[0].equalsIgnoreCase("confirm");
        if (!confirm && !args[0].equalsIgnoreCase("revert")) return invalidSyntax(sender);

        for (ConfigKey<Boolean> key : permissionKeys) {
            try {
                editor.set(key, Boolean.toString(confirm));
            } catch (IOException | ConfigValueException ex) {
                sendMessage(sender, "ERROR", ex.getMessage());
                return false;
            }
        }

        sendMessage(sender, confirm ? "SUCCESSFUL" : "REVERT_SUCCESSFUL");
        return true;
    }
}
