package de.corneliusmay.silkspawners.plugin.commands.executors;

import de.corneliusmay.silkspawners.plugin.commands.SilkSpawnersCommand;
import de.corneliusmay.silkspawners.plugin.commands.StaticTabCompletion;
import de.corneliusmay.silkspawners.plugin.config.handler.ConfigValue;
import de.corneliusmay.silkspawners.plugin.config.PluginConfig;
import org.bukkit.command.CommandSender;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.Locale;
import java.util.MissingResourceException;

public class LocaleCommand extends SilkSpawnersCommand {

    public LocaleCommand() {
        super("locale", true, new StaticTabCompletion( "setting", "reload", "update"));
    }

    @Override
    protected boolean execute(CommandSender sender, String[] args) {
        switch (args.length) {
            case 1 -> {
                switch (args[0].toLowerCase()) {
                    case "reload" -> {
                        try {
                            plugin.getLocale().loadLocale();
                            sender.sendMessage(getMessage("RELOAD_SUCCESSFUL"));
                        } catch (MalformedURLException | MissingResourceException ex) {
                            ex.printStackTrace();
                            sender.sendMessage(getMessage("RELOAD_ERROR"));
                        }
                    }
                    case "setting" -> sender.sendMessage(getMessage("SETTING", new ConfigValue<Locale>(PluginConfig.MESSAGE_LOCALE).get().toString(), plugin.getLocale().getAvailableLocales()));
                    case "update" -> sender.sendMessage(getMessage("UPDATE_WARNING"));
                    default -> invalidSyntax(sender);
                }
            }
            case 2 -> {
                if(!args[0].equalsIgnoreCase("update")) return invalidSyntax(sender);
                if(!args[1].equalsIgnoreCase("confirm")) return invalidSyntax(sender);
                try {
                    plugin.getLocale().copyDefaultLocales(true);
                    plugin.getLocale().loadLocale();
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
