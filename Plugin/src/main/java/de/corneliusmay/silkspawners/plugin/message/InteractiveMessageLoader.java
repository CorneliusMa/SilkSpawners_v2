package de.corneliusmay.silkspawners.plugin.message;

import de.corneliusmay.silkspawners.plugin.loader.ComponentLoader;
import de.corneliusmay.silkspawners.plugin.utils.Logger;
import de.corneliusmay.silkspawners.spi.message.InteractiveMessenger;
import de.corneliusmay.silkspawners.wiring.Loader;
import de.corneliusmay.silkspawners.wiring.Provides;
import de.corneliusmay.silkspawners.wiring.Wired;

@Wired
public class InteractiveMessageLoader implements Loader {

    private static final String CHAT_COMPONENT_CLASS = "net.md_5.bungee.api.chat.TextComponent";

    private final ComponentLoader<InteractiveMessenger> loader =
            new ComponentLoader<>(InteractiveMessenger.class, "message");

    private InteractiveMessenger messenger;

    @Provides
    public InteractiveMessenger getMessenger() {
        return messenger;
    }

    @Override
    public boolean load() {
        messenger = loadMessenger();
        Logger.info("Interactive messages are "
                + (messenger instanceof PlainMessageSender ? "unavailable on this server" : "enabled"));
        return true;
    }

    private InteractiveMessenger loadMessenger() {
        if (!supportsInteractiveMessages()) return new PlainMessageSender();
        try {
            return loader.instantiate("bukkit.MessageImplementation");
        } catch (RuntimeException | LinkageError ex) {
            Logger.warn("Failed to enable interactive messages, falling back to plain messages", ex);
            return new PlainMessageSender();
        }
    }

    private boolean supportsInteractiveMessages() {
        try {
            Class.forName(CHAT_COMPONENT_CLASS);
            return true;
        } catch (ClassNotFoundException ex) {
            return false;
        }
    }
}
