package de.corneliusmay.silkspawners.plugin.config;

import de.corneliusmay.silkspawners.plugin.locale.LocaleHandler;
import de.corneliusmay.silkspawners.plugin.utils.Logger;
import de.corneliusmay.silkspawners.plugin.version.VersionChecker;
import java.io.IOException;
import java.util.MissingResourceException;
import lombok.RequiredArgsConstructor;
import org.weftkit.wiring.Singleton;
import org.weftkit.wiring.Wired;

@Wired
@Singleton(lazy = true)
@RequiredArgsConstructor
public class ConfigReloader {

    private final ConfigLoader configLoader;

    private final LocaleHandler localeHandler;

    private final VersionChecker versionChecker;

    public synchronized boolean reload() {
        if (!configLoader.reload()) return false;
        try {
            if (!localeHandler.isSelectedLocaleLoaded()) localeHandler.loadLocale();
        } catch (IOException | MissingResourceException ex) {
            Logger.error("Error loading locale file", ex);
            return false;
        }
        versionChecker.restart();
        return true;
    }
}
