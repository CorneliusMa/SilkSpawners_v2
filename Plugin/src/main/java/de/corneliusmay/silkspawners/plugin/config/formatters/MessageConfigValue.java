package de.corneliusmay.silkspawners.plugin.config.formatters;

import de.corneliusmay.silkspawners.plugin.config.handler.ConfigValueFormatter;
import de.corneliusmay.silkspawners.plugin.utils.MessageRenderer;

public class MessageConfigValue implements ConfigValueFormatter<String> {
    @Override
    public String format(String value) {
        return MessageRenderer.render(value.replaceAll("(?<!\\\\)\\$" , "§").replace("\\$", "$"));
    }
}
