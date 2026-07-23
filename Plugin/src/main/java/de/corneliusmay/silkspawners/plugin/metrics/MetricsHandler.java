package de.corneliusmay.silkspawners.plugin.metrics;

import de.corneliusmay.silkspawners.plugin.locale.LocaleHandler;
import de.corneliusmay.silkspawners.plugin.utils.Logger;
import de.corneliusmay.silkspawners.wiring.Loader;
import de.corneliusmay.silkspawners.wiring.Wired;
import lombok.RequiredArgsConstructor;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.plugin.Plugin;

@Wired
@RequiredArgsConstructor
public class MetricsHandler implements Loader {

    private static final int SERVICE_ID = 15215;

    private final Plugin plugin;

    private final LocaleHandler localeHandler;

    private Metrics metrics;

    // bStats is non-critical telemetry, so a failure here must never back off the whole plugin load
    @Override
    public boolean load() {
        try {
            Logger.info("Starting bStats integration");
            metrics = new Metrics(plugin, SERVICE_ID);
            metrics.addCustomChart(new SimplePie("locale", localeHandler::getLocaleDisplayName));
        } catch (RuntimeException ex) {
            Logger.error("Failed to start bStats integration", ex);
        }
        return true;
    }

    public void stop() {
        if (metrics == null) return;
        Logger.info("Stopping bStats integration");
        metrics.shutdown();
    }
}
