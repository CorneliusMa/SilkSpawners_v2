package de.corneliusmay.silkspawners.plugin.commands.executors;

import de.corneliusmay.silkspawners.plugin.SilkSpawners;
import de.corneliusmay.silkspawners.plugin.commands.SilkSpawnersCommand;
import de.corneliusmay.silkspawners.plugin.commands.StaticTabCompletion;
import org.bukkit.command.CommandSender;

import java.io.IOException;
import java.net.URISyntaxException;

public class LocaleCommand extends SilkSpawnersCommand {

    public LocaleCommand() {
        super("locale", true, new StaticTabCompletion( "update"));
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if(args.length > 3) return invalidSyntax(sender);

        switch (args.length) {
            case 0 -> sender.sendMessage(getMessage("CURRENT", SilkSpawners.getInstance().getPluginConfig().getLocale()));
            case 1 -> {
                if(!args[0].equalsIgnoreCase("update")) return invalidSyntax(sender);
                sender.sendMessage(getMessage("UPDATE_WARNING"));
            }
            case 2 -> {
                if(!args[0].equalsIgnoreCase("update")) return invalidSyntax(sender);
                if(!args[1].equalsIgnoreCase("confirm")) return invalidSyntax(sender);
                try {
                    SilkSpawners.getInstance().getLocale().copyDefaultLocales(true);
                    sender.sendMessage(getMessage("UPDATE_SUCCESSFUL"));
                } catch (URISyntaxException | IOException ex) {
                    sender.sendMessage(getMessage("UPDATE_ERROR"));
                }
            }
            default -> invalidSyntax(sender);
        }
        return true;
    }
}
