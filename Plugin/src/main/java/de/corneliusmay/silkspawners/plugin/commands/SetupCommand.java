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
import org.weftkit.wiring.Requires;
import org.weftkit.wiring.Wired;

@Wired
@Requires(PluginConfig.class)
public class SetupCommand extends SilkSpawnersCommand {

    private static final List<ConfigKey<Boolean>> PERMISSION_KEYS = List.of(
            PluginConfig.SPAWNER_PERMISSION_DISABLE_DESTROY,
            PluginConfig.SPAWNER_PERMISSION_DISABLE_PLACE,
            PluginConfig.SPAWNER_PERMISSION_DISABLE_CHANGE);

    private final ConfigEditor editor;

    public SetupCommand(ConfigEditor editor) {
        super("setup", true, new StaticTabCompletion("revert"));
        this.editor = editor;
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

        for (ConfigKey<Boolean> key : PERMISSION_KEYS) {
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
