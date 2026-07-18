package de.corneliusmay.silkspawners.plugin.commands;

import de.corneliusmay.silkspawners.plugin.commands.handler.SilkSpawnersCommand;
import de.corneliusmay.silkspawners.plugin.commands.handler.StaticTabCompletion;
import de.corneliusmay.silkspawners.plugin.config.PluginConfig;
import de.corneliusmay.silkspawners.plugin.locale.LocaleHandler;
import de.corneliusmay.silkspawners.wiring.Wired;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.MissingResourceException;
import org.bukkit.command.CommandSender;

@Wired
public class LocaleCommand extends SilkSpawnersCommand {

    private final LocaleHandler localeHandler;

    public LocaleCommand(LocaleHandler localeHandler) {
        super("locale", true, new StaticTabCompletion("setting", "reload", "update"));
        this.localeHandler = localeHandler;
    }

    @Override
    protected boolean execute(CommandSender sender, String[] args) {
        switch (args.length) {
            case 1 -> {
                switch (args[0].toLowerCase()) {
                    case "reload" -> {
                        try {
                            localeHandler.loadLocale();
                            sendMessage(sender, "RELOAD_SUCCESSFUL");
                            if (localeHandler.isIncomplete())
                                sendMessage(
                                        sender,
                                        "INCOMPLETE",
                                        configuredLocale(),
                                        localeHandler.getCompletionPercent(),
                                        LocaleHandler.CROWDIN_URL);
                        } catch (IOException | MissingResourceException ex) {
                            ex.printStackTrace();
                            sendMessage(sender, "RELOAD_ERROR");
                        }
                    }
                    case "setting" -> {
                        String locale = configuredLocale();
                        sendMessage(sender, "SETTING", locale, localeHandler.getAvailableLocales());
                    }
                    case "update" -> sendMessage(sender, "UPDATE_WARNING");
                    default -> invalidSyntax(sender);
                }
            }
            case 2 -> {
                if (!args[0].equalsIgnoreCase("update")) return invalidSyntax(sender);
                if (!args[1].equalsIgnoreCase("confirm")) return invalidSyntax(sender);
                try {
                    localeHandler.copyDefaultLocales(true);
                    localeHandler.loadLocale();
                    sendMessage(sender, "UPDATE_SUCCESSFUL");
                } catch (URISyntaxException | MissingResourceException | IOException ex) {
                    sendMessage(sender, "UPDATE_ERROR");
                }
            }
            default -> invalidSyntax(sender);
        }
        return true;
    }

    private String configuredLocale() {
        return PluginConfig.MESSAGE_LOCALE.get().toString();
    }
}
