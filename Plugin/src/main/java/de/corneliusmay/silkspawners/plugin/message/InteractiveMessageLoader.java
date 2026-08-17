package de.corneliusmay.silkspawners.plugin.message;

import de.corneliusmay.silkspawners.plugin.dump.DumpObject;
import de.corneliusmay.silkspawners.plugin.dump.Dumpable;
import de.corneliusmay.silkspawners.plugin.loader.ComponentLoader;
import de.corneliusmay.silkspawners.plugin.utils.Logger;
import de.corneliusmay.silkspawners.spi.message.InteractiveMessenger;
import lombok.RequiredArgsConstructor;
import org.weftkit.wiring.Loader;
import org.weftkit.wiring.Provides;
import org.weftkit.wiring.Singleton;
import org.weftkit.wiring.Wired;

@Wired
@RequiredArgsConstructor
@Singleton
class InteractiveMessageLoader implements Loader, Dumpable {

    private final Logger logger;

    private static final String CHAT_COMPONENT_CLASS = "net.md_5.bungee.api.chat.TextComponent";

    private final ComponentLoader<InteractiveMessenger> loader =
            new ComponentLoader<>(InteractiveMessenger.class, "message");

    private InteractiveMessenger messenger;

    @Provides
    public InteractiveMessenger getMessenger() {
        return messenger;
    }

    @Override
    public void describe(DumpObject<?> writer) {
        writer.section("interactive-messages")
                .value("supported", supportsInteractiveMessages())
                .value("implementation", messenger.getClass().getName());
    }

    @Override
    public boolean load() {
        messenger = loadMessenger();
        logger.info("Interactive messages are "
                + (messenger instanceof PlainMessageSender ? "unavailable on this server" : "enabled"));
        return true;
    }

    private InteractiveMessenger loadMessenger() {
        if (!supportsInteractiveMessages()) return new PlainMessageSender();
        try {
            return loader.instantiate("bukkit.MessageImplementation");
        } catch (RuntimeException | LinkageError ex) {
            logger.warn("Failed to enable interactive messages, falling back to plain messages", ex);
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
