package de.corneliusmay.silkspawners.plugin.config;

import de.corneliusmay.silkspawners.plugin.config.formatters.BooleanConfigValue;
import de.corneliusmay.silkspawners.plugin.config.formatters.DecimalConfigValue;
import de.corneliusmay.silkspawners.plugin.config.formatters.IntegerConfigValue;
import de.corneliusmay.silkspawners.plugin.config.formatters.MessageConfigValue;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
abstract class ConfigDeclaration {

    final ConfigRegistry registry;

    ConfigKeyBuilder builder(ConfigScope scope, String key) {
        return new ConfigKeyBuilder(registry, scope, key);
    }

    ConfigKey<Boolean> bool(ConfigScope scope, String key, boolean def) {
        return builder(scope, key).def(def).formatter(new BooleanConfigValue());
    }

    ConfigKey<Integer> integer(ConfigScope scope, String key, int def, int min, int max) {
        return builder(scope, key).def(def).formatter(new IntegerConfigValue(min, max));
    }

    ConfigKey<Double> decimal(ConfigScope scope, String key, double def, double min, double max) {
        return builder(scope, key).def(def).formatter(new DecimalConfigValue(min, max));
    }

    ConfigKey<String> message(ConfigScope scope, String key, String def) {
        return builder(scope, key).def(def).formatter(new MessageConfigValue());
    }
}
