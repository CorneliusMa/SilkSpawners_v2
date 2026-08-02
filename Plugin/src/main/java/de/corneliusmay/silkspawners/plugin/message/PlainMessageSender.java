package de.corneliusmay.silkspawners.plugin.message;

import de.corneliusmay.silkspawners.spi.message.InteractiveMessage;
import de.corneliusmay.silkspawners.spi.message.InteractiveMessenger;
import org.bukkit.entity.Player;

public class PlainMessageSender implements InteractiveMessenger {

    static String plain(InteractiveMessage message) {
        return message.prefix() + message.link() + message.suffix();
    }

    @Override
    public void send(Player player, InteractiveMessage message) {
        player.sendMessage(plain(message));
    }
}
