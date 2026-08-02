package de.corneliusmay.silkspawners.spi.message;

import org.bukkit.entity.Player;

public interface InteractiveMessenger {

    void send(Player player, InteractiveMessage message);
}
