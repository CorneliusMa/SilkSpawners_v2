package de.corneliusmay.silkspawners.plugin.config.formatters;

import de.corneliusmay.silkspawners.plugin.config.handler.ConfigValueException;
import de.corneliusmay.silkspawners.plugin.config.handler.ConfigValueFormatter;
import de.corneliusmay.silkspawners.plugin.utils.MessageRenderer;
import de.corneliusmay.silkspawners.plugin.utils.MixedFormattingException;

public class MessageConfigValue implements ConfigValueFormatter<String> {
    @Override
    public String format(String value) {
        try {
            return MessageRenderer.render(value.replaceAll("(?<!\\\\)\\$", "§").replace("\\$", "$"));
        } catch (MixedFormattingException ex) {
            throw new ConfigValueException(ex.getMessage(), ex);
        }
    }
}
