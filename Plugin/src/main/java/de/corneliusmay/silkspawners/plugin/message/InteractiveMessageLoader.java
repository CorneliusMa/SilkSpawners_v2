package de.corneliusmay.silkspawners.plugin.message;

import de.corneliusmay.silkspawners.plugin.capability.Capabilities;
import de.corneliusmay.silkspawners.plugin.loader.ComponentLoader;
import de.corneliusmay.silkspawners.spi.message.InteractiveMessenger;
import lombok.RequiredArgsConstructor;
import org.weftkit.wiring.Loader;
import org.weftkit.wiring.Provides;
import org.weftkit.wiring.Singleton;
import org.weftkit.wiring.Wired;

@Wired
@RequiredArgsConstructor
@Singleton
class InteractiveMessageLoader implements Loader {

    private static final String CHAT_COMPONENT_CLASS = "net.md_5.bungee.api.chat.TextComponent";

    private final Capabilities capabilities;

    private final ComponentLoader<InteractiveMessenger> loader =
            new ComponentLoader<>(InteractiveMessenger.class, "message");

    private InteractiveMessenger messenger;

    @Provides
    public InteractiveMessenger getMessenger() {
        return messenger;
    }

    @Override
    public boolean load() {
        messenger = capabilities
                .probe(
                        "Interactive messages",
                        CHAT_COMPONENT_CLASS,
                        () -> loader.instantiate("bukkit.MessageImplementation"))
                .orElse(new PlainMessageSender());
        return true;
    }
}
