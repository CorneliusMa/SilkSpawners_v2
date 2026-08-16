package de.corneliusmay.silkspawners.message.bukkit;

import de.corneliusmay.silkspawners.spi.message.ClickAction;
import de.corneliusmay.silkspawners.spi.message.InteractiveMessage;
import de.corneliusmay.silkspawners.spi.message.InteractiveMessenger;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

public class MessageImplementation implements InteractiveMessenger {

    @Override
    public void send(Player player, InteractiveMessage message) {
        ClickAction action = message.action();
        ClickEvent click = new ClickEvent(clickEventAction(action), action.value());
        HoverEvent hover =
                new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("§7" + action.value()));
        String linkColors = org.bukkit.ChatColor.getLastColors(message.prefix());

        TextComponent component = new TextComponent();
        append(component, message.prefix(), null, null);
        append(component, linkColors + message.link(), click, hover);
        append(component, linkColors + message.suffix(), null, null);
        player.spigot().sendMessage(component);
    }

    private void append(TextComponent parent, String legacy, ClickEvent click, HoverEvent hover) {
        if (legacy.isEmpty()) return;
        TextComponent part = new TextComponent(TextComponent.fromLegacyText(legacy));
        part.setClickEvent(click);
        part.setHoverEvent(hover);
        parent.addExtra(part);
    }

    private ClickEvent.Action clickEventAction(ClickAction action) {
        return switch (action.type()) {
            case RUN_COMMAND -> ClickEvent.Action.RUN_COMMAND;
            case OPEN_URL -> ClickEvent.Action.OPEN_URL;
        };
    }
}
