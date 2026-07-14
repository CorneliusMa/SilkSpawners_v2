package de.corneliusmay.silkspawners.plugin.commands;

import de.corneliusmay.silkspawners.plugin.commands.handler.SilkSpawnersCommand;
import de.corneliusmay.silkspawners.plugin.commands.handler.StaticTabCompletion;
import java.util.function.BooleanSupplier;
import org.bukkit.command.CommandSender;

public class ConfigCommand extends SilkSpawnersCommand {

    private final BooleanSupplier configReloader;

    public ConfigCommand(BooleanSupplier configReloader) {
        super("config", true, new StaticTabCompletion("reload"));
        this.configReloader = configReloader;
    }

    @Override
    protected boolean execute(CommandSender sender, String[] args) {
        if (args.length != 1 || !args[0].equalsIgnoreCase("reload")) return invalidSyntax(sender);

        if (configReloader.getAsBoolean()) sendMessage(sender, "RELOAD_SUCCESSFUL");
        else sendMessage(sender, "RELOAD_ERROR");
        return true;
    }
}
