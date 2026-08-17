package de.corneliusmay.silkspawners.plugin.listeners;

import de.corneliusmay.silkspawners.plugin.config.ConfigKey;
import de.corneliusmay.silkspawners.plugin.locale.LocaleHandler;
import de.corneliusmay.silkspawners.plugin.spawner.policy.SpawnerTypeProfile;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.weftkit.wiring.Singleton;
import org.weftkit.wiring.Wired;

@Wired
@Singleton
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class DenyMessageHandler {

    private final LocaleHandler locale;

    void destroy(SpawnerTypeProfile profile, Player player) {
        send(player, profile.messageDenyDestroy(), profile.localeKey("DESTROY_DENIED"));
    }

    void place(SpawnerTypeProfile profile, Player player) {
        send(player, profile.messageDenyPlace(), profile.localeKey("PLACE_DENIED"));
    }

    void change(SpawnerTypeProfile profile, Player player) {
        send(player, profile.messageDenyChange(), profile.localeKey("CHANGE_DENIED"));
    }

    private void send(Player player, ConfigKey<Boolean> toggle, String localeKey) {
        if (toggle.get()) player.sendMessage(locale.getMessage(localeKey));
    }
}
