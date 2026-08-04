package de.corneliusmay.silkspawners.plugin.message;

import de.corneliusmay.silkspawners.plugin.locale.LocaleHandler;
import de.corneliusmay.silkspawners.plugin.utils.Logger;
import de.corneliusmay.silkspawners.spi.message.ClickAction;
import de.corneliusmay.silkspawners.spi.message.InteractiveMessage;
import de.corneliusmay.silkspawners.spi.message.InteractiveMessenger;
import java.util.MissingResourceException;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.weftkit.wiring.Wired;

@Wired
@RequiredArgsConstructor
public class InteractiveMessages {

    private static final String LINK_TOKEN = "%link%";

    private final LocaleHandler locale;

    private final InteractiveMessenger messenger;

    public void send(CommandSender sender, ClickAction action, String key, Object... args) {
        String body = locale.getMessage(key, args);
        String link = linkText(key, args);
        int index = body.indexOf(LINK_TOKEN);
        if (index == -1 || link == null) {
            Logger.warn("Interactive message " + key + " has no clickable link ("
                    + (link == null ? "missing _LINK entry" : "missing " + LINK_TOKEN + " placeholder")
                    + "), sending it as plain text");
            sender.sendMessage(body.replace(LINK_TOKEN, ""));
            return;
        }
        InteractiveMessage message = new InteractiveMessage(
                body.substring(0, index), link, body.substring(index + LINK_TOKEN.length()), action);
        if (sender instanceof Player player) messenger.send(player, message);
        else sender.sendMessage(PlainMessageSender.plain(message));
    }

    private String linkText(String key, Object... args) {
        try {
            return locale.getMessageClean(key + "_LINK", args);
        } catch (MissingResourceException ex) {
            return null;
        }
    }
}
