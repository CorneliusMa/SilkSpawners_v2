package de.corneliusmay.silkspawners.plugin.commands.executors;

import de.corneliusmay.silkspawners.plugin.SilkSpawners;
import de.corneliusmay.silkspawners.plugin.commands.SilkSpawnersCommand;
import de.corneliusmay.silkspawners.plugin.commands.StaticTabCompletion;
import org.bukkit.command.CommandSender;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.MissingResourceException;

public class LocaleCommand extends SilkSpawnersCommand {

    public LocaleCommand() {
        super("locale", true, new StaticTabCompletion( "reload", "update"));
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if(args.length > 3) return invalidSyntax(sender);

        switch (args.length) {
            case 0 -> sender.sendMessage(getMessage("CURRENT", SilkSpawners.getInstance().getPluginConfig().getLocale()));
            case 1 -> {
                switch (args[0].toLowerCase()) {
                    case "reload" -> {
                        try {
                            SilkSpawners.getInstance().getLocale().loadLocale();
                            sender.sendMessage(getMessage("RELOAD_SUCCESSFUL"));
                        } catch (MalformedURLException | MissingResourceException ex) {
                            ex.printStackTrace();
                            sender.sendMessage(getMessage("RELOAD_ERROR"));
                        }
                    }
                    case "update" -> sender.sendMessage(getMessage("UPDATE_WARNING"));
                    default -> invalidSyntax(sender);
                }
            }
            case 2 -> {
                if(!args[0].equalsIgnoreCase("update")) return invalidSyntax(sender);
                if(!args[1].equalsIgnoreCase("confirm")) return invalidSyntax(sender);
                try {
                    SilkSpawners.getInstance().getLocale().copyDefaultLocales(true);
                    SilkSpawners.getInstance().getLocale().loadLocale();
                    sender.sendMessage(getMessage("UPDATE_SUCCESSFUL"));
                } catch (URISyntaxException | MissingResourceException | IOException ex) {
                    sender.sendMessage(getMessage("UPDATE_ERROR"));
                }
            }
            default -> invalidSyntax(sender);
        }
        return true;
    }
}
