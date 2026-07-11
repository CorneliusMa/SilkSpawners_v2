package de.corneliusmay.silkspawners.plugin.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.text.MessageFormat;

public class MessageRenderer {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    private static final LegacyComponentSerializer SERIALIZER = createSerializer();

    // §x hex sequences only render correctly on servers with hex chat support, added in 1.16 with ChatColor#of
    private static LegacyComponentSerializer createSerializer() {
        try {
            Class.forName("net.md_5.bungee.api.ChatColor").getMethod("of", String.class);
            return LegacyComponentSerializer.builder().hexColors().useUnusualXRepeatedCharacterHexFormat().build();
        } catch(ReflectiveOperationException ex) {
            return LegacyComponentSerializer.legacySection();
        }
    }

    public static String render(String message) {
        if(message == null) return null;
        if(isMixed(message)) throw new MixedFormattingException();
        if(message.indexOf('§') != -1) return message;
        return SERIALIZER.serialize(MINI_MESSAGE.deserialize(message));
    }

    public static String render(String template, Object... args) {
        if(isMixed(template)) throw new MixedFormattingException();
        if(template.indexOf('§') != -1) return MessageFormat.format(template, args);

        // Args are inserted as components because they may contain legacy codes, which MiniMessage rejects
        TagResolver.Builder resolver = TagResolver.builder();
        for(int i = 0; i < args.length; i++) resolver.resolver(Placeholder.component("arg" + i, asComponent(args[i])));
        return SERIALIZER.serialize(MINI_MESSAGE.deserialize(template.replaceAll("\\{(\\d+)\\}", "<arg$1>"), resolver.build()));
    }

    private static Component asComponent(Object arg) {
        if(arg instanceof String && ((String) arg).indexOf('§') != -1) return SERIALIZER.deserialize((String) arg);
        return Component.text(String.valueOf(arg));
    }

    // A string carrying both legacy codes and a resolved MiniMessage tag cannot be rendered as either format alone.
    // stripTags is used so only tags MiniMessage actually recognizes count, not usage hints like <command> or <Player>.
    private static boolean isMixed(String message) {
        if(message.indexOf('§') == -1 || message.indexOf('<') == -1) return false;
        String withoutLegacy = stripLegacy(message);
        return !MINI_MESSAGE.stripTags(withoutLegacy).equals(withoutLegacy);
    }

    private static String stripLegacy(String message) {
        StringBuilder result = new StringBuilder(message.length());
        for(int i = 0; i < message.length(); i++) {
            char c = message.charAt(i);
            if(c == '§' && i + 1 < message.length()) {
                i++;
                continue;
            }
            result.append(c);
        }
        return result.toString();
    }
}
